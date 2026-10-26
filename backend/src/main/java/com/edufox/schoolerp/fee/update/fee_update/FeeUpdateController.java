package com.edufox.schoolerp.fee.update.fee_update;

import com.edufox.schoolerp.common.persistence.SessionResolver;
import com.edufox.schoolerp.fee.report.fee_booklet.*;
import com.edufox.schoolerp.fee.report.fee_reports.*;
import com.edufox.schoolerp.fee.shared.fee_core.*;
import com.edufox.schoolerp.fee.update.optional_fee.*;
import com.edufox.schoolerp.fee.work.fee_work.*;

import com.edufox.schoolerp.config.DatabaseWriteGuard;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/** Direct Java handlers for the legacy Fee -> Updates pages. */
@RestController
@RequestMapping("/api/fees/update")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class FeeUpdateController {
    private final JdbcTemplate jdbc;
    private final DatabaseWriteGuard writeGuard;

    private static Claims claims(Authentication a){return (Claims)a.getDetails();}
    private static String school(Authentication a){return text(claims(a).get("schoolId",String.class));}
    private static String staff(Authentication a){return text(claims(a).get("staffUid",String.class));}
    private static String text(Object v){return v==null?"":String.valueOf(v).trim();}
    private static int integer(Object v){return text(v).isBlank()?0:Integer.parseInt(text(v));}
    private static BigDecimal decimal(Object v){return text(v).isBlank()?BigDecimal.ZERO:new BigDecimal(text(v));}
    private static Map<String,Object> out(Object...v){Map<String,Object>m=new LinkedHashMap<>();for(int i=0;i+1<v.length;i+=2)m.put(String.valueOf(v[i]),v[i+1]);return m;}
    private String session(Object requested,String sc){return SessionResolver.resolve(jdbc, requested, sc);}

    @GetMapping("/concession-groups")
    public Map<String,Object> concessionGroupData(@RequestParam(required=false)String session,@RequestParam(required=false)Integer classId,@RequestParam(required=false)String admissionNo,Authentication a){
        String sc=school(a),ses=session(session,sc);StringBuilder q=new StringBuilder("SELECT sm.stu_id AS studentId,sm.registration_no AS admissionNo,COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'') AS studentName,COALESCE(cn.class_name,'')+' ('+COALESCE(cl.section,'')+')' AS className,COALESCE(g.group_code,'') AS groupCode,COALESCE(g.Group_name,'') AS groupName FROM TB_StudentMaster sm JOIN student_other_info so ON so.school_id=sm.school_id AND so.registration_no=sm.registration_no LEFT JOIN class_name cn ON cn.school_id=sm.school_id AND cn.id=sm.class_id LEFT JOIN TB_Class cl ON cl.school_id=sm.school_id AND cl.sec_id=sm.sec_id LEFT JOIN fee_student_con_group sg ON sg.school_id=sm.school_id AND sg.stu_id=sm.stu_id AND sg.session=? AND sg.status=1 LEFT JOIN concession_group_name g ON g.school_id=sg.school_id AND g.session=sg.session AND g.group_code=sg.group_code WHERE sm.school_id=? AND sm.session=? AND sm.status='Registered'");List<Object>p=new ArrayList<>(List.of(ses,sc,ses));if(classId!=null&&classId>0){q.append(" AND sm.sec_id=?");p.add(classId);}if(!text(admissionNo).isBlank()){q.append(" AND sm.registration_no LIKE ?");p.add("%"+text(admissionNo)+"%");}q.append(" ORDER BY studentName,sm.stu_id");return out("session",ses,"groups",jdbc.queryForList("SELECT group_code AS groupCode,Group_name AS name FROM concession_group_name WHERE school_id=? AND session=? ORDER BY Group_name",sc,ses),"students",jdbc.queryForList(q.toString(),p.toArray()));
    }

    @PostMapping("/concession-groups") @Transactional
    public Map<String,Object> assignConcessionGroup(@RequestBody Map<String,Object>r,Authentication a){
        writeGuard.requireWritable();String sc=school(a),ses=session(r.get("session"),sc),group=text(r.get("groupCode"));int sid=integer(r.get("studentId"));if(sid<=0||group.isBlank())throw new IllegalArgumentException("Student and concession group are required");
        jdbc.update("UPDATE fee_student_con_group SET status=0,modified_date=GETDATE(),modified_by=? WHERE school_id=? AND session=? AND stu_id=? AND status=1",staff(a),sc,ses,sid);
        jdbc.update("INSERT INTO fee_student_con_group(stu_id,group_code,school_id,created_date,created_by,status,session,quota) VALUES(?,?,?,GETDATE(),?,1,?,?)",sid,group,sc,staff(a),ses,text(r.get("quota")));
        applyGroup(sc,ses,sid,group);
        return out("message","Concession group assigned","studentId",sid,"groupCode",group);
    }

    @DeleteMapping("/concession-groups") @Transactional
    public Map<String,Object> removeConcessionGroup(@RequestParam String session,@RequestParam int studentId,@RequestParam String groupCode,Authentication a){
        writeGuard.requireWritable();String sc=school(a),ses=session(session,sc);jdbc.update("UPDATE fee_student_con_group SET status=0,modified_date=GETDATE(),modified_by=? WHERE school_id=? AND session=? AND stu_id=? AND group_code=?",staff(a),sc,ses,studentId,groupCode);List<Map<String,Object>> details=jdbc.queryForList("SELECT fee_head_id AS feeHeadId,installment_no AS installmentNo FROM concession_group_details WHERE school_id=? AND session=? AND group_code=?",sc,ses,groupCode);for(Map<String,Object>d:details)jdbc.update("UPDATE fee_student_ledger SET concession=0,payable=COALESCE(head_amount,0),balance=COALESCE(head_amount,0)-COALESCE(receive_amount,0) WHERE school_id=? AND session=? AND stu_id=? AND fee_head_id=? AND installment_no=? AND COALESCE(receive_amount,0)=0",sc,ses,studentId,d.get("feeHeadId"),d.get("installmentNo"));return out("message","Concession group removed");
    }

    @GetMapping("/reconciliation")
    public List<Map<String,Object>> reconciliation(@RequestParam(required=false)String session,@RequestParam(required=false)String admissionNo,@RequestParam(required=false)String from,@RequestParam(required=false)String to,@RequestParam(required=false)Integer classId,Authentication a){
        String sc=school(a),ses=session(session,sc);StringBuilder q=new StringBuilder("SELECT row_id AS id,COALESCE(receipt_no,receiptno) AS receiptNo,admno,COALESCE(pay_date,paymentdate,deposit_date) AS paymentDate,COALESCE(pay_mode,mop,'') AS mop,transaction_no AS transactionNo,amount,COALESCE(reconsillation_status,'Pending') AS reconciliationStatus,COALESCE(Bounce_reason,'') AS bounceReason,COALESCE(cheque_bounce_amt,'0') AS bounceCharges,uid FROM fee_payment_detail WHERE school_id=? AND session=? AND LOWER(COALESCE(status,'')) IN ('1','active')");List<Object>p=new ArrayList<>(List.of(sc,ses));if(classId!=null&&classId>0){q.append(" AND EXISTS(SELECT 1 FROM TB_StudentMaster sm WHERE sm.school_id=fee_payment_detail.school_id AND sm.sec_id=? AND sm.registration_no=fee_payment_detail.admno)");p.add(classId);}if(!text(admissionNo).isBlank()){q.append(" AND admno LIKE ?");p.add("%"+text(admissionNo)+"%");}if(!text(from).isBlank()){q.append(" AND TRY_CONVERT(date,COALESCE(pay_date,paymentdate,deposit_date))>=TRY_CONVERT(date,?)");p.add(from);}if(!text(to).isBlank()){q.append(" AND TRY_CONVERT(date,COALESCE(pay_date,paymentdate,deposit_date))<=TRY_CONVERT(date,?)");p.add(to);}return jdbc.queryForList(q+" ORDER BY row_id DESC",p.toArray());
    }

    @PostMapping("/reconciliation") @Transactional
    public Map<String,Object> updateReconciliation(@RequestBody Map<String,Object>r,Authentication a){
        writeGuard.requireWritable();String sc=school(a),ses=session(r.get("session"),sc),status=text(r.get("status")),receipt=text(r.get("receiptNo"));if(receipt.isBlank()||status.isBlank())throw new IllegalArgumentException("Receipt and status are required");String uid=text(r.get("uid"));if(uid.isBlank()){List<Map<String,Object>>x=jdbc.queryForList("SELECT TOP 1 uid FROM fee_payment_detail WHERE school_id=? AND session=? AND COALESCE(receipt_no,receiptno)=?",sc,ses,receipt);if(!x.isEmpty())uid=text(x.get(0).get("uid"));}jdbc.update("UPDATE fee_payment_detail SET reconsillation_status=?,reconsillation_date=GETDATE(),reconsil_BY=?,Bounce_reason=?,cheque_bounce_amt=? WHERE school_id=? AND session=? AND COALESCE(receipt_no,receiptno)=?",status,staff(a),text(r.get("reason")),text(r.get("bounceCharges")),sc,ses,receipt);if("bounce".equalsIgnoreCase(status)||"not approved".equalsIgnoreCase(status)){for(Map<String,Object>d:jdbc.queryForList("SELECT stu_id,inst_no,fee_head_id,receive_amt FROM stu_headwise_deposit WHERE school_id=? AND uid=? AND receipt_no=? AND status='active'",sc,uid,receipt))jdbc.update("UPDATE fee_student_ledger SET receive_amount=CASE WHEN COALESCE(receive_amount,0)>=? THEN receive_amount-? ELSE 0 END,balance=COALESCE(balance,0)+? WHERE school_id=? AND stu_id=? AND installment_no=? AND fee_head_id=?",decimal(d.get("receive_amt")),decimal(d.get("receive_amt")),decimal(d.get("receive_amt")),sc,d.get("stu_id"),d.get("inst_no"),d.get("fee_head_id"));jdbc.update("UPDATE stu_headwise_deposit SET status='Inactive' WHERE school_id=? AND uid=? AND receipt_no=?",sc,uid,receipt);jdbc.update("UPDATE fee_installmentwise_deposit SET status='Inactive' WHERE school_id=? AND uid=?",sc,uid);jdbc.update("UPDATE fee_payment_detail SET status='Inactive',delete_type='Bounce',active_deactive_by=?,active_deactive_date=GETDATE() WHERE school_id=? AND uid=? AND COALESCE(receipt_no,receiptno)=?",staff(a),sc,uid,receipt);}return out("message","Reconciliation status updated","status",status);
    }

    @GetMapping("/student-fee")
    public Map<String,Object> studentFee(@RequestParam String admissionNo,@RequestParam(required=false)String session,@RequestParam(required=false)Integer feeTypeId,Authentication a){
        String sc=school(a),ses=session(session,sc);List<Map<String,Object>>student=jdbc.queryForList("SELECT TOP 1 sm.stu_id AS studentId,sm.registration_no AS admissionNo,sm.class_id AS classId,sm.sec_id AS sectionId,COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'') AS name,COALESCE(so.father_name,'') AS fatherName FROM TB_StudentMaster sm JOIN student_other_info so ON so.school_id=sm.school_id AND so.registration_no=sm.registration_no WHERE sm.school_id=? AND sm.session=? AND sm.registration_no=?",sc,ses,admissionNo);if(student.isEmpty())throw new IllegalArgumentException("Student not found");Map<String,Object>st=student.get(0);StringBuilder q=new StringBuilder("SELECT l.id,l.installment_no AS installmentNo,l.fee_head_id AS feeHeadId,l.fee_type_id AS feeTypeId,COALESCE(h.fee_head,'') AS feeHead,COALESCE(l.head_amount,0) AS headAmount,COALESCE(l.concession,0) AS concession,COALESCE(l.receive_amount,0) AS receiveAmount,COALESCE(l.balance,0) AS balance FROM fee_student_ledger l LEFT JOIN fee_head_master h ON h.school_id=l.school_id AND h.session=l.session AND h.fee_head_id=l.fee_head_id WHERE l.school_id=? AND l.session=? AND l.stu_id=?");List<Object>params=new ArrayList<>(List.of(sc,ses,st.get("studentId")));if(feeTypeId!=null){if(feeTypeId==0)q.append(" AND l.fee_type_id NOT IN (6,7)");else q.append(" AND l.fee_type_id=?");if(feeTypeId!=0)params.add(feeTypeId);}return out("session",ses,"student",st,"rows",jdbc.queryForList(q+" ORDER BY l.installment_no,l.fee_head_id",params.toArray()));
    }

    @PostMapping("/student-fee") @Transactional
    public Map<String,Object> saveStudentFee(@RequestBody Map<String,Object>r,Authentication a){
        writeGuard.requireWritable();String sc=school(a),ses=session(r.get("session"),sc);int sid=integer(r.get("studentId")),classId=integer(r.get("classId"));Object raw=r.get("rows");if(!(raw instanceof List<?> list))throw new IllegalArgumentException("Fee rows are required");int changed=0;for(Object value:list){if(!(value instanceof Map<?,?>m))continue;int inst=integer(m.get("installmentNo")),head=integer(m.get("feeHeadId")),type=integer(m.get("feeTypeId"));boolean enabled="true".equalsIgnoreCase(text(m.get("enabled")))||"1".equals(text(m.get("enabled")));if(enabled){Map<String,Object>charge=jdbc.queryForMap("SELECT TOP 1 fee_charges FROM fee_head_charge WHERE school_id=? AND session=? AND fee_head_id=? AND class_id=? AND status=1",sc,ses,head,classId);BigDecimal amount=decimal(charge.get("fee_charges"));if(amount.signum()>0){int n=jdbc.update("UPDATE fee_student_ledger SET head_amount=?,payable=CASE WHEN COALESCE(concession,0)>0 THEN ?-COALESCE(concession,0) ELSE ? END,balance=CASE WHEN COALESCE(receive_amount,0)>0 THEN (CASE WHEN COALESCE(concession,0)>0 THEN ?-COALESCE(concession,0) ELSE ? END)-COALESCE(receive_amount,0) ELSE (CASE WHEN COALESCE(concession,0)>0 THEN ?-COALESCE(concession,0) ELSE ? END) END WHERE school_id=? AND session=? AND stu_id=? AND installment_no=? AND fee_head_id=?",amount,amount,amount,amount,amount,amount,amount,sc,ses,sid,inst,head);if(n==0)jdbc.update("INSERT INTO fee_student_ledger(stu_id,installment_no,fee_head_id,fee_type_id,head_amount,concession,payable,receive_amount,balance,school_id,session,class_id) VALUES(?,?,?,?,?,0,?,0,?,?,?,?)",sid,inst,head,type,amount,amount,amount,sc,ses,classId);changed++;}}else{changed+=jdbc.update("DELETE FROM fee_student_ledger WHERE school_id=? AND session=? AND stu_id=? AND installment_no=? AND fee_head_id=? AND COALESCE(receive_amount,0)=0",sc,ses,sid,inst,head);}}return out("message","Student fee updated","changed",changed);
    }

    private void applyGroup(String sc,String ses,int sid,String group){
        for(Map<String,Object>d:jdbc.queryForList("SELECT installment_no AS installmentNo,fee_head_id AS feeHeadId,con_type AS type,con_value AS value FROM concession_group_details WHERE school_id=? AND session=? AND group_code=?",sc,ses,group)){
            BigDecimal value=decimal(d.get("value"));String type=text(d.get("type"));String expression="Per".equalsIgnoreCase(type)||"Percentage".equalsIgnoreCase(type)?"COALESCE(l.head_amount,0)*?/100":"?";
            jdbc.update("UPDATE l SET concession="+expression+",payable=COALESCE(l.head_amount,0)-("+expression+"),balance=COALESCE(l.head_amount,0)-("+expression+")-COALESCE(l.receive_amount,0) FROM fee_student_ledger l WHERE l.school_id=? AND l.session=? AND l.stu_id=? AND l.installment_no=? AND l.fee_head_id=?",value,value,value,sc,ses,sid,d.get("installmentNo"),d.get("feeHeadId"));
        }
    }
}
