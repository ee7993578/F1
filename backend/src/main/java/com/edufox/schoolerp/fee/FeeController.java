package com.edufox.schoolerp.fee;

import com.edufox.schoolerp.config.DatabaseWriteGuard;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/** Direct SQL replacement for the legacy fee procedures. */
@RestController
@RequestMapping("/api/fees")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class FeeController {
    private final JdbcTemplate jdbc;
    private final DatabaseWriteGuard writeGuard;

    private static Claims claims(Authentication a){return (Claims)a.getDetails();}
    private static String school(Authentication a){return claims(a).get("schoolId",String.class);}
    private static String staff(Authentication a){return claims(a).get("staffUid",String.class);}
    private static String s(Object v){return v==null?"":String.valueOf(v).trim();}
    private static int i(Object v){return s(v).isBlank()?0:Integer.parseInt(s(v));}
    private static BigDecimal money(Object v){return s(v).isBlank()?BigDecimal.ZERO:new BigDecimal(s(v));}
    private static Map<String,Object> row(Object...v){Map<String,Object>m=new LinkedHashMap<>();for(int n=0;n+1<v.length;n+=2)m.put(String.valueOf(v[n]),v[n+1]);return m;}

    @GetMapping("/bootstrap")
    public Map<String,Object> bootstrap(@RequestParam(required=false)String session,Authentication a){
        String sc=school(a),ses=s(session);if(ses.isBlank())ses=defaultSession(sc);Map<String,Object>o=new LinkedHashMap<>();o.put("session",ses);
        o.put("sessions",jdbc.queryForList("SELECT id,session AS value,session AS label,status FROM TB_Session WHERE school_id=? ORDER BY id DESC",sc));
        o.put("classes",jdbc.queryForList("SELECT c.sec_id AS id,c.class_id AS classId,c.section,cn.class_name AS className,cn.class_name+' ('+c.section+')' AS label FROM TB_Class c JOIN class_name cn ON cn.id=c.class_id AND cn.school_id=c.school_id WHERE c.school_id=? ORDER BY c.class_id,c.sec_id",sc));
        o.put("feeTypes",jdbc.queryForList("SELECT fee_type_id AS id,fee_type AS name,fee_type AS label,installment_no AS installmentNo FROM fee_type_master ORDER BY fee_type_id"));
        o.put("feeHeads",jdbc.queryForList("SELECT row_id AS id,fee_head_id AS feeHeadId,fee_head AS name,fee_head AS feeHead,fee_type_id AS feeTypeId,fee_code AS feeCode,[order] AS sortOrder,status,isOptional,session FROM fee_head_master WHERE school_id=? AND session=? ORDER BY ISNULL([order],999),fee_head",sc,ses));
        o.put("mops",jdbc.queryForList("SELECT id,pay_mode AS name,pay_mode AS label,pay_mode_type AS type FROM Tb_mopmaster ORDER BY id"));
        o.put("banks",jdbc.queryForList("SELECT Bank_Id AS id,BankName AS name,BankName AS label FROM TB_Bank WHERE COALESCE(Display,'1') NOT IN ('0','false','False','No') ORDER BY BankName"));
        o.put("miscHeads",jdbc.queryForList("SELECT miscellaneous_type_id AS id,miscellaneous_type AS name,miscellaneous_type AS label FROM fee_miscellaneous_head WHERE school_id=? AND session=? ORDER BY miscellaneous_type",sc,ses));return o;
    }

    @GetMapping("/students/search")
    public List<Map<String,Object>> searchStudents(@RequestParam(required=false)String query,@RequestParam(required=false)Integer classId,@RequestParam(required=false)Integer sectionId,@RequestParam(required=false)String session,Authentication a){
        String sc=school(a),ses=s(session);if(ses.isBlank())ses=defaultSession(sc);StringBuilder q=new StringBuilder("""
            SELECT TOP (100) sm.stu_id AS id,sm.stu_id AS studentId,sm.uid,sm.registration_no AS admNo,sm.registration_no AS registrationNo,
            UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'')))) AS name,
            UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'')))) AS studentName,
            COALESCE(so.father_name,'') AS father,COALESCE(so.father_name,'') AS fatherName,COALESCE(so.mobile,'') AS mobile,
            COALESCE(cn.class_name,'') AS className,COALESCE(cl.section,'') AS sectionName,COALESCE(cn.class_name,'')+' ('+COALESCE(cl.section,'')+')' AS classLabel,
            sm.class_id AS classId,sm.sec_id AS sectionId,sm.session,sm.status,so.studentimage AS photo,so.studentimage AS imagePath
            FROM TB_StudentMaster sm JOIN student_other_info so ON so.registration_no=sm.registration_no AND so.school_id=sm.school_id
            LEFT JOIN class_name cn ON cn.id=sm.class_id AND cn.school_id=sm.school_id LEFT JOIN TB_Class cl ON cl.sec_id=sm.sec_id AND cl.school_id=sm.school_id WHERE sm.school_id=? AND sm.session=?
            """);List<Object>p=new ArrayList<>(List.of(sc,ses));if(classId!=null){q.append(" AND sm.class_id=?");p.add(classId);}if(sectionId!=null){q.append(" AND sm.sec_id=?");p.add(sectionId);}if(query!=null&&!query.isBlank()){q.append(" AND (sm.registration_no LIKE ? OR CAST(sm.stu_id AS varchar(50)) LIKE ? OR so.first_name LIKE ? OR so.last_name LIKE ? OR so.father_name LIKE ? OR so.mobile LIKE ?)");String v="%"+query.trim()+"%";p.addAll(List.of(v,v,v,v,v,v));}q.append(" ORDER BY so.first_name,so.last_name,sm.stu_id");return jdbc.queryForList(q.toString(),p.toArray());
    }

    @GetMapping("/students/{studentId}/dues")
    public Map<String,Object> dues(@PathVariable String studentId,@RequestParam(required=false)String session,Authentication a){
        String sc=school(a),ses=s(session);if(ses.isBlank())ses=defaultSession(sc);List<Map<String,Object>>cur=jdbc.queryForList("""
            SELECT l.id,l.stu_id AS studentId,l.installment_no AS installmentNo,CAST(l.installment_no AS varchar(30)) AS instNo,l.fee_head_id AS feeHeadId,COALESCE(h.fee_head,'') AS headName,l.fee_type_id AS feeTypeId,COALESCE(l.head_amount,0) AS headAmount,COALESCE(l.concession,0) AS concession,COALESCE(l.payable,0) AS payable,COALESCE(l.receive_amount,0) AS receiveAmount,COALESCE(l.balance,0) AS balance,COALESCE(l.balance,0) AS dueAmount,COALESCE(i.due_date,GETDATE()) AS dueDate,COALESCE(i.installment_dis,'') AS installmentName
            FROM fee_student_ledger l LEFT JOIN fee_head_master h ON h.fee_head_id=l.fee_head_id AND h.school_id=l.school_id AND h.session=l.session LEFT JOIN Installment_Master i ON i.installment_no=l.installment_no AND i.fee_head_id=l.fee_head_id AND i.fee_type_id=l.fee_type_id AND i.school_id=l.school_id AND i.session=l.session
            WHERE l.school_id=? AND l.session=? AND CAST(l.stu_id AS varchar(50))=? AND COALESCE(l.balance,0)>0 ORDER BY l.installment_no,l.fee_head_id
            """,sc,ses,studentId);List<Map<String,Object>>prev=jdbc.queryForList("SELECT id,Registration_No AS registrationNo,session,fee_head AS headName,inst_no AS instNo,COALESCE(payable,0) AS payable,COALESCE(receive_amount,0) AS receiveAmount,COALESCE(latefee,0) AS lateFee,COALESCE(concession,0) AS concession,COALESCE(balance,0) AS balance FROM fee_previousdues WHERE school_id=? AND Registration_No=(SELECT registration_no FROM TB_StudentMaster WHERE school_id=? AND CAST(stu_id AS varchar(50))=?) AND COALESCE(balance,0)>0 ORDER BY session,inst_no",sc,sc,studentId);return row("session",ses,"current",cur,"previous",prev);
    }

    @GetMapping("/students/{studentId}/summary")
    public List<Map<String,Object>> summary(@PathVariable String studentId,Authentication a){return jdbc.queryForList("SELECT l.session,l.installment_no AS installmentNo,COALESCE(h.fee_head,'') AS headName,l.fee_head_id AS feeHeadId,l.fee_type_id AS feeTypeId,COALESCE(l.head_amount,0) AS headAmount,COALESCE(l.concession,0) AS concession,COALESCE(l.receive_amount,0) AS receiveAmount,COALESCE(l.balance,0) AS balance FROM fee_student_ledger l LEFT JOIN fee_head_master h ON h.fee_head_id=l.fee_head_id AND h.school_id=l.school_id AND h.session=l.session WHERE l.school_id=? AND CAST(l.stu_id AS varchar(50))=? ORDER BY l.session DESC,l.installment_no,l.fee_head_id",school(a),studentId);}

    @GetMapping("/students/{studentId}/transactions")
    public List<Map<String,Object>> transactions(@PathVariable String studentId,Authentication a){return jdbc.queryForList("SELECT row_id AS id,session,COALESCE(receipt_no,receiptno) AS receiptNo,COALESCE(pay_date,paymentdate,deposit_date) AS paymentDate,COALESCE(pay_mode,mop,'') AS mop,transaction_no AS transactionNo,transaction_date AS transactionDate,TRY_CONVERT(decimal(18,2),amount) AS amount,status,deposit_by AS depositBy,fee_type_id AS feeTypeId FROM fee_payment_detail WHERE school_id=? AND (CAST(stu_id AS varchar(50))=? OR admission_no=(SELECT registration_no FROM TB_StudentMaster WHERE school_id=? AND CAST(stu_id AS varchar(50))=?)) ORDER BY row_id DESC",school(a),studentId,school(a),studentId);}

    @GetMapping("/reports/collection")
    public List<Map<String,Object>> collection(@RequestParam(required=false)String session,@RequestParam(required=false)String from,@RequestParam(required=false)String to,@RequestParam(required=false)String mop,@RequestParam(required=false)String admissionNo,Authentication a){
        String sc=school(a);StringBuilder q=new StringBuilder("SELECT ROW_NUMBER() OVER(ORDER BY COALESCE(p.pay_date,p.paymentdate),p.row_id) AS sno,p.admission_no AS admNo,COALESCE(so.first_name,'')+' '+COALESCE(so.last_name,'') AS name,COALESCE(cn.class_name,'') AS className,COALESCE(cl.section,'') AS sectionName,p.fee_type AS feeType,COALESCE(p.pay_mode,p.mop,'') AS payMode,p.deposit_by AS depositBy,TRY_CONVERT(decimal(18,2),p.amount) AS amount,COALESCE(p.receipt_no,p.receiptno) AS receiptNo,COALESCE(p.pay_date,p.paymentdate) AS paymentDate,p.status FROM fee_payment_detail p LEFT JOIN TB_StudentMaster sm ON sm.school_id=p.school_id AND (CAST(sm.stu_id AS varchar(50))=p.stu_id OR sm.registration_no=p.admission_no) LEFT JOIN student_other_info so ON so.registration_no=sm.registration_no AND so.school_id=sm.school_id LEFT JOIN TB_Class cl ON cl.sec_id=sm.sec_id AND cl.school_id=sm.school_id LEFT JOIN class_name cn ON cn.id=sm.class_id AND cn.school_id=sm.school_id WHERE p.school_id=?");List<Object>p=new ArrayList<>(List.of(sc));if(!s(session).isBlank()){q.append(" AND p.session=?");p.add(session);}if(!s(from).isBlank()){q.append(" AND CONVERT(date,COALESCE(p.pay_date,p.paymentdate))>=CONVERT(date,?)");p.add(from);}if(!s(to).isBlank()){q.append(" AND CONVERT(date,COALESCE(p.pay_date,p.paymentdate))<=CONVERT(date,?)");p.add(to);}if(!s(mop).isBlank()){q.append(" AND COALESCE(p.pay_mode,p.mop)=?");p.add(mop);}if(!s(admissionNo).isBlank()){q.append(" AND p.admission_no LIKE ?");p.add("%"+admissionNo.trim()+"%");}return jdbc.queryForList(q+" ORDER BY COALESCE(p.pay_date,p.paymentdate),p.row_id",p.toArray());
    }

    @GetMapping("/reports/defaulter")
    public List<Map<String,Object>> defaulter(@RequestParam(required=false)String session,@RequestParam(required=false)Integer classId,Authentication a){String sc=school(a),ses=s(session);if(ses.isBlank())ses=defaultSession(sc);String q="SELECT sm.registration_no AS admNo,sm.stu_id AS studentId,UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.last_name,'')))) AS studentName,COALESCE(cn.class_name,'') AS className,COALESCE(cl.section,'') AS sectionName,SUM(COALESCE(l.balance,0)) AS balance FROM fee_student_ledger l JOIN TB_StudentMaster sm ON sm.stu_id=l.stu_id AND sm.school_id=l.school_id LEFT JOIN student_other_info so ON so.registration_no=sm.registration_no AND so.school_id=sm.school_id LEFT JOIN class_name cn ON cn.id=sm.class_id AND cn.school_id=sm.school_id LEFT JOIN TB_Class cl ON cl.sec_id=sm.sec_id AND cl.school_id=sm.school_id WHERE l.school_id=? AND l.session=? AND COALESCE(l.balance,0)>0";List<Object>p=new ArrayList<>(List.of(sc,ses));if(classId!=null){q+=" AND sm.class_id=?";p.add(classId);}return jdbc.queryForList(q+" GROUP BY sm.registration_no,sm.stu_id,so.first_name,so.last_name,cn.class_name,cl.section ORDER BY cn.class_name,cl.section,studentName",p.toArray());}

    @GetMapping("/heads") public List<Map<String,Object>> heads(@RequestParam(required=false)String session,Authentication a){String ses=s(session);if(ses.isBlank())ses=defaultSession(school(a));return jdbc.queryForList("SELECT row_id AS id,fee_head_id AS feeHeadId,fee_head AS name,fee_type_id AS feeTypeId,fee_code AS feeCode,[order] AS sortOrder,status,isOptional,session FROM fee_head_master WHERE school_id=? AND session=? ORDER BY ISNULL([order],999),fee_head",school(a),ses);}
    @GetMapping("/charges") public List<Map<String,Object>> charges(@RequestParam(required=false)String session,@RequestParam(required=false)Integer classId,Authentication a){String ses=s(session);if(ses.isBlank())ses=defaultSession(school(a));String q="SELECT c.fee_charge_id AS id,c.fee_head_id AS feeHeadId,c.fee_type_id AS feeTypeId,c.fee_charges AS charges,c.class_id AS classId,c.status,c.session FROM fee_head_charge c WHERE c.school_id=? AND c.session=?";List<Object>p=new ArrayList<>(List.of(school(a),ses));if(classId!=null){q+=" AND c.class_id=?";p.add(classId);}return jdbc.queryForList(q+" ORDER BY c.class_id,c.fee_head_id",p.toArray());}
    @GetMapping("/installments") public List<Map<String,Object>> installments(@RequestParam(required=false)String session,Authentication a){String ses=s(session);if(ses.isBlank())ses=defaultSession(school(a));return jdbc.queryForList("SELECT row_id AS id,installment_no AS installmentNo,fee_type_id AS feeTypeId,fee_head_id AS feeHeadId,month_id AS monthId,due_date AS dueDate,status,installment_dis AS description,inst_code AS code,session FROM Installment_Master WHERE school_id=? AND session=? ORDER BY installment_no,fee_head_id",school(a),ses);}

    @GetMapping("/setup/bootstrap")
    public Map<String,Object> setupBootstrap(@RequestParam(required=false)String session,Authentication a){
        String sc=school(a),ses=s(session);if(ses.isBlank())ses=defaultSession(sc);
        return row("session",ses,
            "sessions",jdbc.queryForList("SELECT session AS value,session AS label FROM TB_Session WHERE school_id=? ORDER BY id DESC",sc),
            "feeTypes",jdbc.queryForList("SELECT fee_type_id AS id,fee_type AS name,installment_no AS installmentNo FROM fee_type_master ORDER BY fee_type_id"),
            "months",jdbc.queryForList("SELECT month_id AS id,month_name AS name FROM tb_month ORDER BY id"),
            "heads",heads(ses,a),"charges",charges(ses,null,a),"installments",installments(ses,a),
            "lateFees",lateFees(ses,null,a),"concessionGroups",concessionGroups(ses,a),
            "concessionDetails",concessionDetails(ses,null,a));
    }

    @GetMapping("/late-fees")
    public List<Map<String,Object>> lateFees(@RequestParam(required=false)String session,@RequestParam(required=false)Integer feeTypeId,Authentication a){
        String sc=school(a),ses=s(session);if(ses.isBlank())ses=defaultSession(sc);
        String q="SELECT late_fee_id AS id,inst_no AS installmentNo,from_date AS fromDate,to_date AS toDate,mode,charge,status,fee_type_id AS feeTypeId,session FROM late_fee WHERE school_id=? AND session=?";
        List<Object> p=new ArrayList<>(List.of(sc,ses));if(feeTypeId!=null){q+=" AND fee_type_id=?";p.add(feeTypeId);}
        return jdbc.queryForList(q+" ORDER BY inst_no,from_date",p.toArray());
    }

    @GetMapping("/concession-groups")
    public List<Map<String,Object>> concessionGroups(@RequestParam(required=false)String session,Authentication a){
        String ses=s(session);if(ses.isBlank())ses=defaultSession(school(a));
        return jdbc.queryForList("SELECT id,group_code AS groupCode,Group_name AS name,main_group_id AS mainGroupId,is_late_fee_applicable AS lateFeeApplicable,session FROM concession_group_name WHERE school_id=? AND session=? ORDER BY Group_name",school(a),ses);
    }

    @GetMapping("/concession-details")
    public List<Map<String,Object>> concessionDetails(@RequestParam(required=false)String session,@RequestParam(required=false)String groupCode,Authentication a){
        String ses=s(session);if(ses.isBlank())ses=defaultSession(school(a));String q="SELECT d.id,d.group_code AS groupCode,d.con_type AS type,d.installment_no AS installmentNo,d.fee_type_id AS feeTypeId,d.fee_head_id AS feeHeadId,COALESCE(h.fee_head,'') AS feeHead,COALESCE(d.con_value,'') AS value,COALESCE(i.installment_dis,'') AS label,d.session FROM concession_group_details d LEFT JOIN fee_head_master h ON h.fee_head_id=d.fee_head_id AND h.school_id=d.school_id AND h.session=d.session LEFT JOIN Installment_Master i ON i.installment_no=d.installment_no AND i.fee_head_id=d.fee_head_id AND i.fee_type_id=d.fee_type_id AND i.school_id=d.school_id AND i.session=d.session WHERE d.school_id=? AND d.session=?";List<Object>p=new ArrayList<>(List.of(school(a),ses));if(!s(groupCode).isBlank()){q+=" AND d.group_code=?";p.add(groupCode);}return jdbc.queryForList(q+" ORDER BY d.group_code,d.installment_no,d.fee_head_id",p.toArray());
    }

    @PostMapping("/charges") @Transactional
    public Map<String,Object> addCharge(@RequestBody Map<String,Object>r,Authentication a){
        writeGuard.requireWritable();String sc=school(a),ses=s(r.get("session"));
        jdbc.update("INSERT INTO fee_head_charge(fee_charge_id,fee_head_id,fee_type_id,fee_charges,class_id,status,created_date,created_by,school_id,session,school_type,transport_type_id) VALUES((SELECT ISNULL(MAX(fee_charge_id),0)+1 FROM fee_head_charge WHERE school_id=?),?,?,?,?,1,GETDATE(),?,?,?,?,?)",sc,r.get("feeHeadId"),r.get("feeTypeId"),r.get("charges"),r.get("classId"),staff(a),sc,ses,r.get("schoolType"),r.get("transportTypeId"));
        return row("message","Fee charge saved");
    }
    @PutMapping("/charges/{id}") @Transactional
    public Map<String,Object> updateCharge(@PathVariable int id,@RequestBody Map<String,Object>r,Authentication a){
        writeGuard.requireWritable();jdbc.update("UPDATE fee_head_charge SET fee_head_id=?,fee_type_id=?,fee_charges=?,class_id=?,status=?,modified_date=GETDATE(),modified_by=?,school_type=?,transport_type_id=? WHERE fee_charge_id=? AND school_id=?",r.get("feeHeadId"),r.get("feeTypeId"),r.get("charges"),r.get("classId"),r.get("status"),staff(a),r.get("schoolType"),r.get("transportTypeId"),id,school(a));return row("message","Fee charge updated");
    }
    @DeleteMapping("/charges/{id}") @Transactional
    public Map<String,Object> deleteCharge(@PathVariable int id,Authentication a){writeGuard.requireWritable();jdbc.update("UPDATE fee_head_charge SET status=0,modified_date=GETDATE(),modified_by=? WHERE fee_charge_id=? AND school_id=?",staff(a),id,school(a));return row("message","Fee charge deactivated");}

    @PostMapping("/installments") @Transactional
    public Map<String,Object> addInstallment(@RequestBody Map<String,Object>r,Authentication a){
        writeGuard.requireWritable();String sc=school(a),ses=s(r.get("session"));jdbc.update("INSERT INTO Installment_Master(installment_no,fee_type_id,fee_head_id,month_id,due_date,status,created_date,created_by,school_id,session,installment_dis,inst_code,other_installment_dis) VALUES(?,?,?,?,?,1,GETDATE(),?,?,?,?,?,?)",r.get("installmentNo"),r.get("feeTypeId"),r.get("feeHeadId"),r.get("monthId"),r.get("dueDate"),staff(a),sc,ses,s(r.get("description")),s(r.get("code")),s(r.get("otherDescription")));return row("message","Installment saved");
    }
    @PutMapping("/installments/{id}") @Transactional
    public Map<String,Object> updateInstallment(@PathVariable int id,@RequestBody Map<String,Object>r,Authentication a){writeGuard.requireWritable();jdbc.update("UPDATE Installment_Master SET installment_no=?,fee_type_id=?,fee_head_id=?,month_id=?,due_date=?,status=?,modified_date=GETDATE(),modified_by=?,installment_dis=?,inst_code=?,other_installment_dis=? WHERE row_id=? AND school_id=?",r.get("installmentNo"),r.get("feeTypeId"),r.get("feeHeadId"),r.get("monthId"),r.get("dueDate"),r.get("status"),staff(a),s(r.get("description")),s(r.get("code")),s(r.get("otherDescription")),id,school(a));return row("message","Installment updated");}
    @DeleteMapping("/installments/{id}") @Transactional
    public Map<String,Object> deleteInstallment(@PathVariable int id,Authentication a){writeGuard.requireWritable();jdbc.update("UPDATE Installment_Master SET status=0,modified_date=GETDATE(),modified_by=? WHERE row_id=? AND school_id=?",staff(a),id,school(a));return row("message","Installment deactivated");}

    @PostMapping("/late-fees") @Transactional
    public Map<String,Object> addLateFee(@RequestBody Map<String,Object>r,Authentication a){writeGuard.requireWritable();String sc=school(a),ses=s(r.get("session"));jdbc.update("INSERT INTO late_fee(inst_no,from_date,to_date,mode,charge,status,created_date,created_by,school_id,session,fee_type_id) VALUES(?,?,?,?,?,1,GETDATE(),?,?,?,?,?)",r.get("installmentNo"),r.get("fromDate"),r.get("toDate"),s(r.get("mode")),r.get("charge"),staff(a),sc,ses,r.get("feeTypeId"));return row("message","Late fee saved");}
    @PutMapping("/late-fees/{id}") @Transactional
    public Map<String,Object> updateLateFee(@PathVariable int id,@RequestBody Map<String,Object>r,Authentication a){writeGuard.requireWritable();jdbc.update("UPDATE late_fee SET inst_no=?,from_date=?,to_date=?,mode=?,charge=?,status=?,modified_date=GETDATE(),modified_by=?,fee_type_id=? WHERE late_fee_id=? AND school_id=?",r.get("installmentNo"),r.get("fromDate"),r.get("toDate"),s(r.get("mode")),r.get("charge"),r.get("status"),staff(a),r.get("feeTypeId"),id,school(a));return row("message","Late fee updated");}
    @DeleteMapping("/late-fees/{id}") @Transactional
    public Map<String,Object> deleteLateFee(@PathVariable int id,Authentication a){writeGuard.requireWritable();jdbc.update("UPDATE late_fee SET status=0,modified_date=GETDATE(),modified_by=? WHERE late_fee_id=? AND school_id=?",staff(a),id,school(a));return row("message","Late fee deactivated");}

    @PostMapping("/concession-groups") @Transactional
    public Map<String,Object> addConcessionGroup(@RequestBody Map<String,Object>r,Authentication a){writeGuard.requireWritable();String code=s(r.get("groupCode"));if(code.isBlank())code="CG-"+UUID.randomUUID().toString().substring(0,8);String sc=school(a),ses=s(r.get("session"));jdbc.update("INSERT INTO concession_group_name(group_code,Group_name,school_id,created_by,created_date,session,is_late_fee_applicable,main_group_id) VALUES(?,?,?, ?,GETDATE(),?,?,?)",code,s(r.get("name")),sc,staff(a),ses,r.get("lateFeeApplicable"),r.get("mainGroupId"));return row("message","Concession group saved","groupCode",code);}
    @PutMapping("/concession-groups/{id}") @Transactional
    public Map<String,Object> updateConcessionGroup(@PathVariable int id,@RequestBody Map<String,Object>r,Authentication a){writeGuard.requireWritable();jdbc.update("UPDATE concession_group_name SET Group_name=?,is_late_fee_applicable=?,main_group_id=? WHERE id=? AND school_id=?",s(r.get("name")),r.get("lateFeeApplicable"),r.get("mainGroupId"),id,school(a));return row("message","Concession group updated");}
    @DeleteMapping("/concession-groups/{id}") @Transactional
    public Map<String,Object> deleteConcessionGroup(@PathVariable int id,Authentication a){writeGuard.requireWritable();jdbc.update("DELETE FROM concession_group_name WHERE id=? AND school_id=?",id,school(a));return row("message","Concession group deleted");}

    @PostMapping("/concession-details") @Transactional
    public Map<String,Object> addConcessionDetail(@RequestBody Map<String,Object>r,Authentication a){writeGuard.requireWritable();jdbc.update("INSERT INTO concession_group_details(group_code,con_type,installment_no,fee_type_id,fee_head_id,con_value,school_id,created_date,created_by,session) VALUES(?,?,?,?,?,?,?,GETDATE(),?,?)",s(r.get("groupCode")),s(r.get("type")),r.get("installmentNo"),r.get("feeTypeId"),r.get("feeHeadId"),s(r.get("value")),school(a),staff(a),s(r.get("session")));return row("message","Concession value saved");}
    @PutMapping("/concession-details/{id}") @Transactional
    public Map<String,Object> updateConcessionDetail(@PathVariable int id,@RequestBody Map<String,Object>r,Authentication a){writeGuard.requireWritable();jdbc.update("UPDATE concession_group_details SET con_type=?,installment_no=?,fee_type_id=?,fee_head_id=?,con_value=? WHERE id=? AND school_id=?",s(r.get("type")),r.get("installmentNo"),r.get("feeTypeId"),r.get("feeHeadId"),s(r.get("value")),id,school(a));return row("message","Concession value updated");}
    @DeleteMapping("/concession-details/{id}") @Transactional
    public Map<String,Object> deleteConcessionDetail(@PathVariable int id,Authentication a){writeGuard.requireWritable();jdbc.update("DELETE FROM concession_group_details WHERE id=? AND school_id=?",id,school(a));return row("message","Concession value deleted");}

    @GetMapping("/concessions/student/{studentId}")
    public List<Map<String,Object>> studentConcessions(@PathVariable String studentId,@RequestParam(required=false)String session,Authentication a){String ses=s(session);if(ses.isBlank())ses=defaultSession(school(a));return jdbc.queryForList("SELECT c.id,c.stu_id AS studentId,c.inst_no AS installmentNo,c.fee_head_id AS feeHeadId,c.fee_type_id AS feeTypeId,c.con_type AS type,COALESCE(c.con_charge,0) AS charge,COALESCE(c.con_per,'') AS percentage,c.remark,c.status,c.session,COALESCE(h.fee_head,'') AS feeHead FROM fee_concession c LEFT JOIN fee_head_master h ON h.fee_head_id=c.fee_head_id AND h.school_id=c.school_id AND h.session=c.session WHERE c.school_id=? AND c.session=? AND CAST(c.stu_id AS varchar(50))=? ORDER BY c.inst_no,c.fee_head_id",school(a),ses,studentId);}
    @PostMapping("/concessions") @Transactional
    public Map<String,Object> saveStudentConcession(@RequestBody Map<String,Object>r,Authentication a){writeGuard.requireWritable();String sc=school(a),ses=s(r.get("session"));String studentId=s(r.get("studentId"));String type=s(r.get("type"));BigDecimal value=money(r.get("value"));Integer existing=jdbc.queryForObject("SELECT TOP 1 id FROM fee_concession WHERE school_id=? AND session=? AND stu_id=? AND inst_no=? AND fee_head_id=?",Integer.class,sc,ses,studentId,r.get("installmentNo"),r.get("feeHeadId"));if(existing!=null){jdbc.update("UPDATE fee_concession SET con_type=?,con_charge=?,con_per=?,remark=?,status=1,modified_date=GETDATE(),modified_by=? WHERE id=? AND school_id=?",type,"Per".equalsIgnoreCase(type)?BigDecimal.ZERO:value,"Per".equalsIgnoreCase(type)?value.toPlainString():"",s(r.get("remark")),staff(a),existing,sc);}else{jdbc.update("INSERT INTO fee_concession(inst_no,fee_head_id,fee_type_id,con_type,con_charge,con_per,stu_id,status,created_date,created_by,remark,school_id,session) VALUES(?,?,?,?,?,?,?,1,GETDATE(),?,?,?,?,?)",r.get("installmentNo"),r.get("feeHeadId"),r.get("feeTypeId"),type,"Per".equalsIgnoreCase(type)?BigDecimal.ZERO:value,"Per".equalsIgnoreCase(type)?value.toPlainString():"",studentId,staff(a),s(r.get("remark")),sc,ses);}return row("message","Student concession saved");}

    @PostMapping("/deposit") @Transactional
    public Map<String,Object> deposit(@RequestBody Map<String,Object>r,Authentication a){
        writeGuard.requireWritable();String sc=school(a),stf=staff(a),studentId=s(r.get("studentId")),ses=s(r.get("session"));if(ses.isBlank())ses=defaultSession(sc);String payDate=s(r.get("paymentDate"));if(payDate.isBlank())payDate=LocalDate.now().toString();String uid=UUID.randomUUID().toString().substring(0,8),receipt=s(r.get("receiptNo"));if(receipt.isBlank())receipt=nextReceipt(sc,ses);String mop=s(r.get("mop"));if(mop.isBlank())mop="Cash";String txn=s(r.get("transactionNo"));List<?>selected=r.get("dues") instanceof List<?>l?l:List.of();if(selected.isEmpty())throw new IllegalArgumentException("Select at least one installment");BigDecimal total=BigDecimal.ZERO;Map<String,Object>stu=jdbc.queryForMap("SELECT TOP 1 registration_no FROM TB_StudentMaster WHERE school_id=? AND CAST(stu_id AS varchar(50))=?",sc,studentId);String adm=s(stu.get("registration_no"));
        for(Object raw:selected){if(!(raw instanceof Map<?,?>m))continue;int id=i(m.get("id"));BigDecimal amount=money(m.get("amount"));if(id<=0||amount.signum()<=0)continue;
            if("previous".equalsIgnoreCase(s(m.get("source")))){
                Map<String,Object>d=jdbc.queryForMap("SELECT TOP 1 inst_no,NULL AS fee_type_id,balance FROM fee_previousdues WHERE id=? AND school_id=? AND session=?",id,sc,ses);BigDecimal bal=money(d.get("balance"));if(amount.compareTo(bal)>0)throw new IllegalArgumentException("Amount exceeds due balance");int changed=jdbc.update("UPDATE fee_previousdues SET receive_amount=COALESCE(receive_amount,0)+?,balance=COALESCE(balance,0)-? WHERE id=? AND school_id=? AND session=? AND COALESCE(balance,0)>=?",amount,amount,id,sc,ses,amount);if(changed!=1)throw new IllegalArgumentException("Previous fee due changed; reload student dues");jdbc.update("INSERT INTO stu_fee_deposit(stu_id,inst_no,pay_mode,bank_id,pay_status,pay_date,dip_amt,created_date,created_by,school_id,session,uid,transaction_no,transaction_date,late_fee,fee_type,receipt_no,advance_paid,isVerified,transaction_amount,status) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",studentId,s(d.get("inst_no")),mop,s(r.get("bankId")),"Success",payDate,amount,LocalDateTime.now(),stf,sc,ses,uid,txn,payDate,0,d.get("fee_type_id"),receipt,0,true,amount.toPlainString(),"1");total=total.add(amount);continue;
            }
            Map<String,Object>d=jdbc.queryForMap("SELECT TOP 1 installment_no,fee_type_id,balance FROM fee_student_ledger WHERE id=? AND school_id=? AND session=?",id,sc,ses);BigDecimal bal=money(d.get("balance"));if(amount.compareTo(bal)>0)throw new IllegalArgumentException("Amount exceeds due balance");int changed=jdbc.update("UPDATE fee_student_ledger SET receive_amount=COALESCE(receive_amount,0)+?,balance=COALESCE(balance,0)-? WHERE id=? AND school_id=? AND session=? AND COALESCE(balance,0)>=?",amount,amount,id,sc,ses,amount);if(changed!=1)throw new IllegalArgumentException("Fee due changed; reload student dues");jdbc.update("INSERT INTO stu_fee_deposit(stu_id,inst_no,pay_mode,bank_id,pay_status,pay_date,dip_amt,created_date,created_by,school_id,session,uid,transaction_no,transaction_date,late_fee,fee_type,receipt_no,advance_paid,isVerified,transaction_amount,status) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",studentId,s(d.get("installment_no")),mop,s(r.get("bankId")),"Success",payDate,amount,LocalDateTime.now(),stf,sc,ses,uid,txn,payDate,0,d.get("fee_type_id"),receipt,0,true,amount.toPlainString(),"1");total=total.add(amount);}
        jdbc.update("INSERT INTO fee_payment_detail(financial_year,school_id,admno,mop,fee_type,transaction_no,amount,paymentdate,receiptno,uid,status,deposit_by,deposit_date,transaction_date,student_unique_id,admission_no,created_by,created_on,fee_type_id,pay_date,pay_mode,receipt_no,session,stu_id) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",ses,sc,adm,mop,s(r.get("feeType")),txn,total.toPlainString(),payDate,receipt,uid,"1",s(r.get("depositBy")),payDate,payDate,studentId,adm,stf,LocalDateTime.now(),s(r.get("feeType")),payDate,mop,receipt,ses,studentId);return row("message","Fee deposited successfully","receiptNo",receipt,"uid",uid,"amount",total);
    }

    @PostMapping("/heads") public Map<String,Object> addHead(@RequestBody Map<String,Object>r,Authentication a){writeGuard.requireWritable();String sc=school(a),ses=s(r.get("session"));jdbc.update("INSERT INTO fee_head_master(fee_head_id,fee_type_id,fee_head,fee_code,[order],status,created_date,created_by,school_id,session,isOptional) VALUES((SELECT ISNULL(MAX(fee_head_id),0)+1 FROM fee_head_master WHERE school_id=?),?,?,?,?,1,GETDATE(),?,?,?,?)",sc,r.get("feeTypeId"),s(r.get("name")),s(r.get("feeCode")),r.get("sortOrder"),sc,ses,r.get("isOptional"));return row("message","Fee head saved");}
    @PutMapping("/heads/{id}") public Map<String,Object> updateHead(@PathVariable int id,@RequestBody Map<String,Object>r,Authentication a){writeGuard.requireWritable();jdbc.update("UPDATE fee_head_master SET fee_head=?,fee_code=?,fee_type_id=?,[order]=?,status=?,isOptional=?,modified_date=GETDATE(),modified_by=? WHERE row_id=? AND school_id=?",s(r.get("name")),s(r.get("feeCode")),r.get("feeTypeId"),r.get("sortOrder"),r.get("status"),r.get("isOptional"),staff(a),id,school(a));return row("message","Fee head updated");}
    @DeleteMapping("/heads/{id}") public Map<String,Object> deleteHead(@PathVariable int id,Authentication a){writeGuard.requireWritable();jdbc.update("UPDATE fee_head_master SET status=0,modified_date=GETDATE(),modified_by=? WHERE row_id=? AND school_id=?",staff(a),id,school(a));return row("message","Fee head deactivated");}

    private String defaultSession(String sc){List<Map<String,Object>>r=jdbc.queryForList("SELECT TOP 1 session FROM TB_Session WHERE school_id=? ORDER BY CASE WHEN status='Default' THEN 0 ELSE 1 END,id DESC",sc);return r.isEmpty()?"":s(r.get(0).get("session"));}
    private String nextReceipt(String sc,String ses){Integer n=jdbc.queryForObject("SELECT ISNULL(MAX(row_id),0)+1 FROM fee_payment_detail WHERE school_id=? AND session=?",Integer.class,sc,ses);return "R-"+ses.replace("-","")+"-"+(n==null?1:n);}
}
