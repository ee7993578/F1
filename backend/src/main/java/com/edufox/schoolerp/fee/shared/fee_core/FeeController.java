package com.edufox.schoolerp.fee.shared.fee_core;
import com.edufox.schoolerp.fee.report.fee_booklet.*;
import com.edufox.schoolerp.fee.report.fee_reports.*;
import com.edufox.schoolerp.fee.update.fee_update.*;
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
    private final OptionalFeeService optionalFeeService;

    private static Claims claims(Authentication a){return (Claims)a.getDetails();}
    private static String school(Authentication a){return claims(a).get("schoolId",String.class);}
    private static String staff(Authentication a){return claims(a).get("staffUid",String.class);}
    private static String s(Object v){return v==null?"":String.valueOf(v).trim();}
    private static int i(Object v){return s(v).isBlank()?0:Integer.parseInt(s(v));}
    private static BigDecimal money(Object v){return s(v).isBlank()?BigDecimal.ZERO:new BigDecimal(s(v));}
    private static Map<String,Object> row(Object...v){Map<String,Object>m=new LinkedHashMap<>();for(int n=0;n+1<v.length;n+=2)m.put(String.valueOf(v[n]),v[n+1]);return m;}

    @GetMapping("/bootstrap")
    public Map<String,Object> bootstrap(@RequestParam(required=false)String session,Authentication a){
        String sc=school(a),ses=s(session);if(ses.isBlank())ses=defaultSession(sc);Map<String,Object>o=new LinkedHashMap<>();o.put("schoolId",sc);o.put("session",ses);
        Map<String,Object> schoolFlags=jdbc.queryForMap("SELECT TOP 1 COALESCE(transport,0) AS transportEnabled,COALESCE(hostel,0) AS hostelEnabled FROM schoolmaster WHERE school_id=?",sc);
        o.put("transportEnabled",schoolFlags.get("transportEnabled"));o.put("hostelEnabled",schoolFlags.get("hostelEnabled"));
        o.put("sessions",jdbc.queryForList("SELECT id,session AS value,session AS label,status FROM TB_Session WHERE school_id=? ORDER BY id DESC",sc));
        o.put("classes",jdbc.queryForList("SELECT c.sec_id AS id,c.class_id AS classId,c.section,cn.class_name AS className,cn.class_name+' ('+c.section+')' AS label FROM TB_Class c JOIN class_name cn ON cn.id=c.class_id AND cn.school_id=c.school_id WHERE c.school_id=? ORDER BY c.class_id,c.sec_id",sc));
        o.put("feeTypes",jdbc.queryForList("""
            SELECT CAST(0 AS varchar(20)) AS id,'Regular Fee' AS name,'Regular Fee' AS label
            UNION
            SELECT CAST(CASE WHEN sm.transport=1 THEN CASE WHEN c.considerTransport=1 THEN 0 ELSE 6 END ELSE 6 END AS varchar(20)),
                   CASE WHEN sm.transport=1 THEN CASE WHEN c.considerTransport=1 THEN 'Regular Fee' ELSE 'Transport Fee' END ELSE 'Transport Fee' END,
                   CASE WHEN sm.transport=1 THEN CASE WHEN c.considerTransport=1 THEN 'Regular Fee' ELSE 'Transport Fee' END ELSE 'Transport Fee' END
            FROM schoolmaster sm JOIN configuration c ON c.school_id=sm.school_id WHERE sm.school_id=?
            UNION
            SELECT CAST(CASE WHEN sm.hostel=1 THEN CASE WHEN c.considerHostel=0 THEN 7 ELSE 0 END ELSE 0 END AS varchar(20)),
                   CASE WHEN sm.hostel=1 THEN CASE WHEN c.considerHostel=0 THEN 'Hostel Fee' ELSE 'Regular Fee' END ELSE 'Regular Fee' END,
                   CASE WHEN sm.hostel=1 THEN CASE WHEN c.considerHostel=0 THEN 'Hostel Fee' ELSE 'Regular Fee' END ELSE 'Regular Fee' END
            FROM schoolmaster sm JOIN configuration c ON c.school_id=sm.school_id WHERE sm.school_id=?
            ORDER BY id
            """,sc,sc));
        o.put("feeHeads",jdbc.queryForList("SELECT row_id AS id,fee_head_id AS feeHeadId,fee_head AS name,fee_head AS feeHead,fee_type_id AS feeTypeId,fee_code AS feeCode,[order] AS sortOrder,status,isOptional,session FROM fee_head_master WHERE school_id=? AND session=? ORDER BY ISNULL([order],999),fee_head",sc,ses));
        o.put("mops",jdbc.queryForList("SELECT id,pay_mode AS name,pay_mode AS label,pay_mode_type AS type FROM Tb_mopmaster ORDER BY id"));
        o.put("banks",jdbc.queryForList("SELECT Bank_Id AS id,BankName AS name,BankName AS label FROM TB_Bank WHERE COALESCE(Display,'1') NOT IN ('0','false','False','No') ORDER BY BankName"));
        o.put("miscHeads",jdbc.queryForList("SELECT miscellaneous_type_id AS id,miscellaneous_type AS name,miscellaneous_type AS label FROM fee_miscellaneous_head WHERE school_id=? AND session=? ORDER BY miscellaneous_type",sc,ses));return o;
    }

    @GetMapping("/students/search")
    public List<Map<String,Object>> searchStudents(@RequestParam(required=false)String query,@RequestParam(required=false)Integer classId,@RequestParam(required=false)Integer sectionId,@RequestParam(required=false)String feeBookNo,@RequestParam(required=false)String session,Authentication a){
        String sc=school(a),ses=s(session);if(ses.isBlank())ses=defaultSession(sc);StringBuilder q=new StringBuilder("""
            SELECT TOP (100) sm.stu_id AS id,sm.stu_id AS studentId,sm.uid,sm.registration_no AS admNo,sm.registration_no AS registrationNo,
            UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'')))) AS name,
            UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'')))) AS studentName,
            COALESCE(so.father_name,'') AS father,COALESCE(so.father_name,'') AS fatherName,COALESCE(so.mobile,'') AS mobile,
            COALESCE(cn.class_name,'') AS className,COALESCE(cl.section,'') AS sectionName,COALESCE(cn.class_name,'')+' ('+COALESCE(cl.section,'')+')' AS classLabel,
            sm.class_id AS classId,sm.sec_id AS sectionId,sm.FeeBookNo AS feeBookNo,sm.session,sm.status,so.studentimage AS photo,so.studentimage AS imagePath
            FROM TB_StudentMaster sm JOIN student_other_info so ON so.registration_no=sm.registration_no AND so.school_id=sm.school_id
            LEFT JOIN class_name cn ON cn.id=sm.class_id AND cn.school_id=sm.school_id LEFT JOIN TB_Class cl ON cl.sec_id=sm.sec_id AND cl.school_id=sm.school_id WHERE sm.school_id=? AND sm.session=?
            """);List<Object>p=new ArrayList<>(List.of(sc,ses));if(classId!=null){q.append(" AND sm.sec_id=?");p.add(classId);}if(sectionId!=null){q.append(" AND sm.sec_id=?");p.add(sectionId);}if(query!=null&&!query.isBlank()){q.append(" AND (sm.registration_no LIKE ? OR CAST(sm.stu_id AS varchar(50)) LIKE ? OR so.first_name LIKE ? OR so.last_name LIKE ? OR so.father_name LIKE ? OR so.mobile LIKE ?)");String v="%"+query.trim()+"%";p.addAll(List.of(v,v,v,v,v,v));}if(!s(feeBookNo).isBlank()){q.append(" AND sm.FeeBookNo=?");p.add(feeBookNo.trim());}q.append(" ORDER BY so.first_name,so.last_name,sm.stu_id");return jdbc.queryForList(q.toString(),p.toArray());
    }

    @GetMapping("/students/{studentId}/dues")
    public Map<String,Object> dues(@PathVariable String studentId,@RequestParam(required=false)String session,Authentication a){
        String sc=school(a),ses=s(session);if(ses.isBlank())ses=defaultSession(sc);List<Map<String,Object>>cur=jdbc.queryForList("""
            SELECT MIN(l.id) AS id,l.stu_id AS studentId,l.session,l.installment_no AS installmentNo,CAST(l.installment_no AS varchar(30)) AS instNo,MIN(l.fee_head_id) AS feeHeadId,STRING_AGG(COALESCE(h.fee_head,''),', ') AS headName,l.fee_type_id AS feeTypeId,SUM(COALESCE(l.head_amount,0)) AS headAmount,SUM(COALESCE(l.concession,0)) AS concession,SUM(COALESCE(l.payable,0)) AS payable,SUM(COALESCE(l.receive_amount,0)) AS receiveAmount,SUM(COALESCE(l.balance,0)) AS balance,SUM(COALESCE(l.balance,0)) AS dueAmount,MAX(COALESCE(i.due_date,GETDATE())) AS dueDate,MAX(COALESCE(i.installment_dis,'')) AS installmentName,STRING_AGG(CAST(l.id AS varchar(20)),',') AS lineIds
            FROM fee_student_ledger l LEFT JOIN fee_head_master h ON h.fee_head_id=l.fee_head_id AND h.school_id=l.school_id AND h.session=l.session LEFT JOIN Installment_Master i ON i.installment_no=l.installment_no AND i.fee_head_id=l.fee_head_id AND i.fee_type_id=l.fee_type_id AND i.school_id=l.school_id AND i.session=l.session
            WHERE l.school_id=? AND l.session=? AND CAST(l.stu_id AS varchar(50))=? AND COALESCE(l.balance,0)>0 GROUP BY l.stu_id,l.session,l.installment_no,l.fee_type_id ORDER BY l.installment_no,l.fee_type_id
            """,sc,ses,studentId);List<Map<String,Object>>prev=jdbc.queryForList("SELECT id,Registration_No AS registrationNo,session,fee_head AS headName,inst_no AS instNo,COALESCE(payable,0) AS payable,COALESCE(receive_amount,0) AS receiveAmount,COALESCE(latefee,0) AS lateFee,COALESCE(concession,0) AS concession,COALESCE(balance,0) AS balance FROM fee_previousdues WHERE school_id=? AND Registration_No=(SELECT registration_no FROM TB_StudentMaster WHERE school_id=? AND CAST(stu_id AS varchar(50))=?) AND COALESCE(balance,0)>0 ORDER BY session,inst_no",sc,sc,studentId);Map<String,Object> st=jdbc.queryForMap("SELECT TOP 1 registration_no,Admission_date FROM TB_StudentMaster WHERE school_id=? AND CAST(stu_id AS varchar(50))=?",sc,studentId);for(Map<String,Object> due:cur)due.put("lateFee",calculateLateFee(sc,ses,studentId,i(due.get("installmentNo")),i(due.get("feeTypeId")),money(due.get("balance")),s(st.get("Admission_date"))));BigDecimal advance=jdbc.queryForObject("SELECT COALESCE(SUM(balance),0) FROM advance_fee WHERE school_id=? AND Admno=? AND status='active' AND COALESCE(balance,0)>0",BigDecimal.class,sc,s(st.get("registration_no")));return row("session",ses,"current",cur,"previous",prev,"advance",advance==null?BigDecimal.ZERO:advance);
    }

    /**
     * Read model for the six-tab DepositFee.aspx page.  This endpoint keeps
     * the legacy page's fee-type filtering, excluded months, wave-off values,
     * previous-session rows and live advance amount together, while all
     * persistence remains in Java code below (no stored-procedure call).
     */
    @GetMapping("/students/{studentId}/deposit-flow")
    public Map<String,Object> depositFlow(@PathVariable String studentId,@RequestParam(required=false)String session,@RequestParam(required=false)String feeType,Authentication a){
        String sc=school(a),ses=s(session),ft=s(feeType);if(ses.isBlank())ses=defaultSession(sc);if(ft.isBlank())ft="0";
        List<Map<String,Object>> current=jdbc.queryForList("""
            SELECT MIN(l.id) AS id,l.stu_id AS studentId,l.session,l.installment_no AS installmentNo,
                   CAST(l.installment_no AS varchar(30)) AS instNo,MIN(l.fee_head_id) AS feeHeadId,
                   STRING_AGG(COALESCE(h.fee_head,''),', ') AS headName,l.fee_type_id AS feeTypeId,
                   SUM(COALESCE(l.head_amount,0)) AS headAmount,SUM(COALESCE(l.concession,0)) AS concession,
                   SUM(COALESCE(l.payable,0)) AS payable,SUM(COALESCE(l.receive_amount,0)) AS receiveAmount,
                   SUM(COALESCE(l.balance,0)) AS balance,SUM(COALESCE(l.balance,0)) AS dueAmount,
                   MAX(COALESCE(i.due_date,GETDATE())) AS dueDate,MAX(COALESCE(i.installment_dis,'')) AS installmentName,
                    COALESCE(MAX(wo.waveOffFeeValue),0) AS waveOffFeeValue,
                   STRING_AGG(CAST(l.id AS varchar(20)),',') AS lineIds
            FROM fee_student_ledger l
            LEFT JOIN fee_head_master h ON h.fee_head_id=l.fee_head_id AND h.school_id=l.school_id AND h.session=l.session
            LEFT JOIN Installment_Master i ON i.installment_no=l.installment_no AND i.fee_head_id=l.fee_head_id
                AND i.fee_type_id=l.fee_type_id AND i.school_id=l.school_id AND i.session=l.session
             LEFT JOIN (
                 SELECT school_id,session,stu_id,installment_no,fee_type_id,
                        SUM(TRY_CONVERT(decimal(18,2),WaveOffFeeValue)) AS waveOffFeeValue
                 FROM WaveOffFees
                 WHERE status=0
                 GROUP BY school_id,session,stu_id,installment_no,fee_type_id
             ) wo ON wo.school_id=l.school_id AND wo.session=l.session
                 AND wo.stu_id=CAST(l.stu_id AS varchar(50))
                 AND wo.installment_no=CAST(l.installment_no AS varchar(50))
                 AND wo.fee_type_id=CAST(l.fee_type_id AS varchar(50))
            LEFT JOIN configuration cfg ON cfg.school_id=l.school_id
            WHERE l.school_id=? AND l.session=? AND CAST(l.stu_id AS varchar(50))=? AND COALESCE(l.balance,0)>0
              AND NOT EXISTS (SELECT 1 FROM Exempt_FeeType_for_Month ex WHERE ex.school_id=l.school_id AND ex.session=l.session
                AND ex.fee_type_id=l.fee_type_id AND ex.month_id=i.month_id)
              AND ((?='0' AND l.fee_type_id NOT IN (CASE WHEN COALESCE(cfg.considerTransport,0)=0 THEN 6 ELSE -1 END,
                                                     CASE WHEN COALESCE(cfg.considerHostel,0)=0 THEN 7 ELSE -1 END))
                   OR (?<>'0' AND CAST(l.fee_type_id AS varchar(30))=?))
            GROUP BY l.stu_id,l.session,l.installment_no,l.fee_type_id ORDER BY l.installment_no,l.fee_type_id
            """,sc,ses,studentId,ft,ft,ft);
        String previous=previousSession(ses);
        List<Map<String,Object>> prev=jdbc.queryForList("""
            SELECT MIN(id) AS id,Registration_No AS registrationNo,session,MIN(fee_head) AS headName,inst_no AS instNo,
                   SUM(COALESCE(payable,0)) AS payable,SUM(COALESCE(receive_amount,0)) AS receiveAmount,
                   SUM(TRY_CONVERT(decimal(18,2),COALESCE(latefee,'0'))) AS lateFee,SUM(COALESCE(concession,0)) AS concession,
                   SUM(COALESCE(balance,0)) AS balance,
                   SUM(COALESCE(balance,0))+SUM(TRY_CONVERT(decimal(18,2),COALESCE(latefee,'0'))) AS finalCharges,
                   SUM(COALESCE(balance,0)) AS balanceHeadAmt,
                   SUM(TRY_CONVERT(decimal(18,2),COALESCE(latefee,'0'))) AS balanceLateFee
            FROM fee_previousdues WHERE school_id=? AND Registration_No=(SELECT TOP 1 registration_no FROM TB_StudentMaster
                WHERE school_id=? AND CAST(stu_id AS varchar(50))=? AND session IN (?,?)) AND session=? AND COALESCE(balance,0)>0
            GROUP BY Registration_No,session,inst_no ORDER BY session,inst_no
            """,sc,sc,studentId,ses,previous,previous);
        Map<String,Object> st=jdbc.queryForMap("SELECT TOP 1 registration_no,Admission_date FROM TB_StudentMaster WHERE school_id=? AND CAST(stu_id AS varchar(50))=?",sc,studentId);
        for(Map<String,Object> due:current){BigDecimal late=calculateLateFee(sc,ses,studentId,i(due.get("installmentNo")),i(due.get("feeTypeId")),money(due.get("balance")),s(st.get("Admission_date")));due.put("lateFee",late.max(BigDecimal.ZERO));}
        BigDecimal advance=jdbc.queryForObject("SELECT COALESCE(SUM(balance),0) FROM advance_fee WHERE school_id=? AND Admno=? AND status='active' AND COALESCE(balance,0)>0",BigDecimal.class,sc,s(st.get("registration_no")));
        return row("session",ses,"previousSession",previous,"current",current,"previous",prev,"advance",advance==null?BigDecimal.ZERO:advance);
    }

    @GetMapping("/students/{studentId}/summary")
    public List<Map<String,Object>> summary(@PathVariable String studentId,Authentication a){return jdbc.queryForList("SELECT l.session,l.installment_no AS installmentNo,COALESCE(h.fee_head,'') AS headName,l.fee_head_id AS feeHeadId,l.fee_type_id AS feeTypeId,COALESCE(l.head_amount,0) AS headAmount,COALESCE(l.concession,0) AS concession,COALESCE(l.receive_amount,0) AS receiveAmount,COALESCE(l.balance,0) AS balance FROM fee_student_ledger l LEFT JOIN fee_head_master h ON h.fee_head_id=l.fee_head_id AND h.school_id=l.school_id AND h.session=l.session WHERE l.school_id=? AND CAST(l.stu_id AS varchar(50))=? ORDER BY l.session DESC,l.installment_no,l.fee_head_id",school(a),studentId);}

    @GetMapping("/students/{studentId}/transactions")
    public List<Map<String,Object>> transactions(@PathVariable String studentId,Authentication a){
        String sc=school(a);
        List<Map<String,Object>> st=jdbc.queryForList("SELECT TOP 1 registration_no FROM TB_StudentMaster WHERE school_id=? AND CAST(stu_id AS varchar(50))=?",sc,studentId);
        String adm=st.isEmpty()?"":s(st.get(0).get("registration_no"));
        return jdbc.queryForList("""
            WITH headwise AS (
                SELECT receipt_no,uid,stu_id,status,SUM(TRY_CONVERT(decimal(18,2),receive_amt)) AS installmentAmount,
                       STRING_AGG(CAST(inst_no AS varchar(30)),',') AS instNo
                FROM stu_headwise_deposit
                WHERE school_id=? AND stu_id=?
                GROUP BY receipt_no,uid,stu_id,status
            ), installmentwise AS (
                SELECT uid,stu_id,status,SUM(TRY_CONVERT(decimal(18,2),inst_late_fee)) AS lateFee,
                       STRING_AGG(CAST(installment_no AS varchar(30)),',') AS lateInstNo
                FROM fee_installmentwise_deposit
                WHERE school_id=? AND stu_id=?
                GROUP BY uid,stu_id,status
            ), advances AS (
                SELECT uid,SUM(COALESCE(balance,0)) AS advanceAmount
                FROM advance_fee
                WHERE school_id=? AND uid IS NOT NULL
                GROUP BY uid
            )
            SELECT p.row_id AS id,p.session,
                   COALESCE(p.receipt_no,p.receiptno) AS receiptNo,
                   COALESCE(p.pay_date,p.paymentdate,p.deposit_date) AS paymentDate,
                   COALESCE(p.pay_mode,p.mop,'') AS mop,p.transaction_no AS transactionNo,
                   p.transaction_date AS transactionDate,TRY_CONVERT(decimal(18,2),p.amount) AS amount,
                   COALESCE(h.instNo,i.lateInstNo,'') AS installmentNo,
                   COALESCE(h.installmentAmount,TRY_CONVERT(decimal(18,2),p.amount),0) AS installmentAmount,
                   COALESCE(i.lateFee,0) AS lateFee,COALESCE(av.advanceAmount,0) AS advanceAmount,
                   CASE WHEN LOWER(COALESCE(p.status,'')) IN ('1','active') THEN 'active' ELSE 'inactive' END AS status,
                   p.deposit_by AS depositBy,p.fee_type_id AS feeTypeId,p.reconsil_BY AS reconsilBy
            FROM fee_payment_detail p
            LEFT JOIN headwise h ON h.receipt_no=COALESCE(p.receipt_no,p.receiptno) AND h.uid=p.uid
            LEFT JOIN installmentwise i ON i.uid=p.uid
            LEFT JOIN advances av ON av.uid=p.uid
            WHERE p.school_id=? AND (p.stu_id=? OR p.admission_no=? OR p.admno=?)
              AND COALESCE(p.Inactive_Remark,'')!='Chargeback'
            ORDER BY COALESCE(p.pay_date,p.paymentdate,p.deposit_date) DESC,p.row_id DESC
            """,sc,studentId,sc,studentId,sc,sc,studentId,adm,adm);
    }

    @GetMapping("/transactions/{studentId}/receipt")
    public Map<String,Object> receipt(@PathVariable String studentId,@RequestParam String receiptNo,Authentication a){
        String sc=school(a);List<Map<String,Object>> st=jdbc.queryForList("SELECT TOP 1 sm.registration_no AS admNo,UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'')))) AS name,COALESCE(so.father_name,'') AS fatherName FROM TB_StudentMaster sm LEFT JOIN student_other_info so ON so.school_id=sm.school_id AND so.registration_no=sm.registration_no WHERE sm.school_id=? AND CAST(sm.stu_id AS varchar(50))=?",sc,studentId);String adm=st.isEmpty()?"":s(st.get(0).get("admNo"));List<Map<String,Object>> header=jdbc.queryForList("SELECT TOP 1 receipt_no AS receiptNo,COALESCE(pay_date,paymentdate,deposit_date) AS paymentDate,admno,mop,transaction_no AS transactionNo,amount,fee_type,deposit_by AS depositBy,uid FROM fee_payment_detail WHERE school_id=? AND (stu_id=? OR admission_no=? OR admno=?) AND COALESCE(receipt_no,receiptno)=? ORDER BY row_id DESC",sc,studentId,adm,adm,receiptNo);if(header.isEmpty())throw new IllegalArgumentException("Receipt not found");return row("header",header.get(0),"student",st,"lines",jdbc.queryForList("SELECT inst_no AS installmentNo,fee_head_name AS feeHead,receive_amt AS amount FROM stu_headwise_deposit WHERE school_id=? AND uid=? AND status='active' ORDER BY inst_no,fee_head_id",sc,header.get(0).get("uid")));
    }

    @PostMapping("/transactions/delete") @Transactional
    public Map<String,Object> deleteTransaction(@RequestBody Map<String,Object>r,Authentication a){
        writeGuard.requireWritable();String sc=school(a),receipt=s(r.get("receiptNo")),adm=s(r.get("admissionNo")),remark=s(r.get("remark")),session=s(r.get("session"));List<Map<String,Object>> payments=jdbc.queryForList("SELECT uid,stu_id FROM fee_payment_detail WHERE school_id=? AND COALESCE(receipt_no,receiptno)=? AND (?='' OR financial_year=? OR session=?) AND (?='' OR admno=?) AND LOWER(COALESCE(status,'')) IN ('active','1')",sc,receipt,session,session,session,adm,adm);if(payments.isEmpty())throw new IllegalArgumentException("Active receipt not found");int restored=0;for(Map<String,Object> p:payments){String uid=s(p.get("uid"));for(Map<String,Object> d:jdbc.queryForList("SELECT stu_id,inst_no,fee_head_id,receive_amt FROM stu_headwise_deposit WHERE school_id=? AND uid=? AND receipt_no=? AND status='active'",sc,uid,receipt)){restored+=jdbc.update("UPDATE fee_student_ledger SET receive_amount=CASE WHEN COALESCE(receive_amount,0)>=? THEN receive_amount-? ELSE 0 END,balance=COALESCE(balance,0)+? WHERE school_id=? AND stu_id=? AND installment_no=? AND fee_head_id=?",money(d.get("receive_amt")),money(d.get("receive_amt")),money(d.get("receive_amt")),sc,d.get("stu_id"),d.get("inst_no"),d.get("fee_head_id"));}jdbc.update("UPDATE stu_headwise_deposit SET status='Inactive' WHERE school_id=? AND uid=? AND receipt_no=?",sc,uid,receipt);jdbc.update("UPDATE fee_installmentwise_deposit SET status='Inactive' WHERE school_id=? AND uid=?",sc,uid);jdbc.update("UPDATE advance_fee SET status='Inactive' WHERE school_id=? AND uid=? AND status='active'",sc,uid);jdbc.update("UPDATE fee_payment_detail SET status='Inactive',active_deactive_by=?,active_deactive_date=GETDATE(),Inactive_Remark=?,delete_type='manual' WHERE school_id=? AND uid=? AND COALESCE(receipt_no,receiptno)=?",staff(a),remark,sc,uid,receipt);}
        return row("message","Fee deleted successfully","restored",restored);
    }

    @PutMapping("/transactions") @Transactional
    public Map<String,Object> updateTransaction(@RequestBody Map<String,Object>r,Authentication a){
        writeGuard.requireWritable();String sc=school(a),oldReceipt=s(r.get("receiptNo")),newReceipt=s(r.get("newReceiptNo"));if(newReceipt.isBlank())newReceipt=oldReceipt;String date=s(r.get("transactionDate")),payDate=s(r.get("paymentDate"));if(payDate.isBlank())payDate=date;int changed=jdbc.update("UPDATE fee_payment_detail SET receipt_no=?,receiptno=?,mop=?,pay_mode=?,Bank_id=?,transaction_no=?,transaction_date=?,paymentdate=?,pay_date=?,deposit_date=?,Settlement_Date=? WHERE school_id=? AND COALESCE(receipt_no,receiptno)=? AND LOWER(COALESCE(status,'')) IN ('active','1')",newReceipt,newReceipt,s(r.get("mop")),s(r.get("mop")),s(r.get("bankId")),s(r.get("transactionNo")),date,payDate,payDate,payDate,s(r.get("settlementDate")),sc,oldReceipt);jdbc.update("UPDATE stu_headwise_deposit SET receipt_no=?,deposit_date=? WHERE school_id=? AND receipt_no=? AND status='active'",newReceipt,payDate,sc,oldReceipt);return row("message",changed>0?"Student fee data updated successfully":"Receipt not found","updated",changed);
    }

    @GetMapping("/reports/collection")
    public List<Map<String,Object>> collection(@RequestParam(required=false)String session,@RequestParam(required=false)String from,@RequestParam(required=false)String to,@RequestParam(required=false)String mop,@RequestParam(required=false)String admissionNo,Authentication a){
        String sc=school(a);StringBuilder q=new StringBuilder("SELECT ROW_NUMBER() OVER(ORDER BY COALESCE(p.pay_date,p.paymentdate),p.row_id) AS sno,p.admission_no AS admNo,COALESCE(so.first_name,'')+' '+COALESCE(so.last_name,'') AS name,COALESCE(cn.class_name,'') AS className,COALESCE(cl.section,'') AS sectionName,p.fee_type AS feeType,COALESCE(p.pay_mode,p.mop,'') AS payMode,p.deposit_by AS depositBy,TRY_CONVERT(decimal(18,2),p.amount) AS amount,COALESCE(p.receipt_no,p.receiptno) AS receiptNo,COALESCE(p.pay_date,p.paymentdate) AS paymentDate,p.status FROM fee_payment_detail p LEFT JOIN TB_StudentMaster sm ON sm.school_id=p.school_id AND (CAST(sm.stu_id AS varchar(50))=p.stu_id OR sm.registration_no=p.admission_no) LEFT JOIN student_other_info so ON so.registration_no=sm.registration_no AND so.school_id=sm.school_id LEFT JOIN TB_Class cl ON cl.sec_id=sm.sec_id AND cl.school_id=sm.school_id LEFT JOIN class_name cn ON cn.id=sm.class_id AND cn.school_id=sm.school_id WHERE p.school_id=?");List<Object>p=new ArrayList<>(List.of(sc));if(!s(session).isBlank()){q.append(" AND p.session=?");p.add(session);}if(!s(from).isBlank()){q.append(" AND CONVERT(date,COALESCE(p.pay_date,p.paymentdate))>=CONVERT(date,?)");p.add(from);}if(!s(to).isBlank()){q.append(" AND CONVERT(date,COALESCE(p.pay_date,p.paymentdate))<=CONVERT(date,?)");p.add(to);}if(!s(mop).isBlank()){q.append(" AND COALESCE(p.pay_mode,p.mop)=?");p.add(mop);}if(!s(admissionNo).isBlank()){q.append(" AND p.admission_no LIKE ?");p.add("%"+admissionNo.trim()+"%");}return jdbc.queryForList(q+" ORDER BY COALESCE(p.pay_date,p.paymentdate),p.row_id",p.toArray());
    }

    @GetMapping("/reports/defaulter")
    public List<Map<String,Object>> defaulter(@RequestParam(required=false)String session,@RequestParam(required=false)Integer classId,Authentication a){String sc=school(a),ses=s(session);if(ses.isBlank())ses=defaultSession(sc);String q="SELECT sm.registration_no AS admNo,sm.stu_id AS studentId,UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.last_name,'')))) AS studentName,COALESCE(cn.class_name,'') AS className,COALESCE(cl.section,'') AS sectionName,SUM(COALESCE(l.balance,0)) AS balance FROM fee_student_ledger l JOIN TB_StudentMaster sm ON sm.stu_id=l.stu_id AND sm.school_id=l.school_id LEFT JOIN student_other_info so ON so.registration_no=sm.registration_no AND so.school_id=sm.school_id LEFT JOIN class_name cn ON cn.id=sm.class_id AND cn.school_id=sm.school_id LEFT JOIN TB_Class cl ON cl.sec_id=sm.sec_id AND cl.school_id=sm.school_id WHERE l.school_id=? AND l.session=? AND COALESCE(l.balance,0)>0";List<Object>p=new ArrayList<>(List.of(sc,ses));if(classId!=null){q+=" AND sm.class_id=?";p.add(classId);}return jdbc.queryForList(q+" GROUP BY sm.registration_no,sm.stu_id,so.first_name,so.last_name,cn.class_name,cl.section ORDER BY cn.class_name,cl.section,studentName",p.toArray());}

    @GetMapping("/heads") public List<Map<String,Object>> heads(@RequestParam(required=false)String session,Authentication a){String ses=s(session);if(ses.isBlank())ses=defaultSession(school(a));return jdbc.queryForList("SELECT row_id AS id,fee_head_id AS feeHeadId,fee_head AS name,fee_type_id AS feeTypeId,fee_code AS feeCode,[order] AS sortOrder,status,isOptional,session FROM fee_head_master WHERE school_id=? AND session=? ORDER BY ISNULL([order],999),fee_head",school(a),ses);}
    @GetMapping("/charges") public List<Map<String,Object>> charges(@RequestParam(required=false)String session,@RequestParam(required=false)Integer classId,@RequestParam(required=false)Integer schoolType,Authentication a){String sc=school(a),ses=s(session);if(ses.isBlank())ses=defaultSession(sc);List<Object>p=new ArrayList<>();int cls=classId==null?0:classId,typ=schoolType==null?1:schoolType;String q="SELECT hm.fee_head_id AS feeHeadId,hm.fee_type_id AS feeTypeId,hm.fee_head AS feeHead,COALESCE(ft.fee_type,'') AS feeType,COALESCE(fc.fee_charge_id,0) AS id,COALESCE(fc.fee_charges,0) AS charges,COALESCE(fc.class_id,?) AS classId,COALESCE(fc.school_type,?) AS schoolType,COALESCE(fc.status,1) AS status,hm.session,CASE WHEN fc.fee_charge_id IS NULL THEN CAST(0 AS bit) ELSE CAST(1 AS bit) END AS hasCharge FROM fee_head_master hm LEFT JOIN fee_type_master ft ON ft.fee_type_id=hm.fee_type_id LEFT JOIN fee_head_charge fc ON fc.school_id=hm.school_id AND fc.session=hm.session AND fc.class_id=? AND fc.fee_head_id=hm.fee_head_id AND fc.school_type=? AND fc.status=1 WHERE hm.school_id=? AND hm.session=? AND hm.status=1 AND hm.fee_type_id NOT IN (6,7)";p.add(cls);p.add(typ);p.add(cls);p.add(typ);p.add(sc);p.add(ses);return jdbc.queryForList(q+" ORDER BY ISNULL(hm.[order],999),hm.fee_head",p.toArray());}
    @GetMapping("/installments") public List<Map<String,Object>> installments(@RequestParam(required=false)String session,@RequestParam(required=false)Integer feeTypeId,Authentication a){String sc=school(a),ses=s(session);if(ses.isBlank())ses=defaultSession(sc);String q="SELECT i.row_id AS id,i.installment_no AS installmentNo,i.fee_type_id AS feeTypeId,i.fee_head_id AS feeHeadId,COALESCE(h.fee_head,'') AS feeHead,COALESCE(ft.fee_type,'') AS feeType,i.month_id AS monthId,COALESCE(m.month_name,'') AS monthName,i.due_date AS dueDate,i.status,i.installment_dis AS description,i.inst_code AS code,i.session FROM Installment_Master i LEFT JOIN fee_head_master h ON h.school_id=i.school_id AND h.session=i.session AND h.fee_head_id=i.fee_head_id LEFT JOIN fee_type_master ft ON ft.fee_type_id=i.fee_type_id LEFT JOIN tb_month m ON m.month_id=i.month_id WHERE i.school_id=? AND i.session=?";List<Object>p=new ArrayList<>(List.of(sc,ses));if(feeTypeId!=null){q+=" AND i.fee_type_id=?";p.add(feeTypeId);}return jdbc.queryForList(q+" ORDER BY i.installment_no,i.fee_type_id,i.fee_head_id,i.month_id",p.toArray());}

    @GetMapping("/setup/bootstrap")
    public Map<String,Object> setupBootstrap(@RequestParam(required=false)String session,Authentication a){
        String sc=school(a),ses=s(session);if(ses.isBlank())ses=defaultSession(sc);
        Integer schoolType=jdbc.queryForObject("SELECT TOP 1 schoolType FROM schoolmaster WHERE school_id=?",Integer.class,sc);if(schoolType==null)schoolType=1;
        return row("session",ses,"schoolType",schoolType,
            "sessions",jdbc.queryForList("SELECT session AS value,session AS label FROM TB_Session WHERE school_id=? ORDER BY id DESC",sc),
            "classes",jdbc.queryForList("SELECT id,class_name AS name,class_name AS label FROM class_name WHERE school_id=? ORDER BY id",sc),
            "mainGroups",jdbc.queryForList("SELECT main_group_id AS id,main_group_name AS name FROM main_group_name WHERE school_id=? AND IsActive=1 ORDER BY main_group_name",sc),
            "feeTypes",jdbc.queryForList("SELECT fee_type_id AS id,fee_type AS name,installment_no AS installmentNo FROM fee_type_master ORDER BY fee_type_id"),
            "months",jdbc.queryForList("SELECT month_id AS id,month_name AS name FROM tb_month ORDER BY id"),
            "heads",heads(ses,a),"charges",charges(ses,null,null,a),"installments",installments(ses,null,a),
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
        return jdbc.queryForList("SELECT g.id,g.group_code AS groupCode,g.Group_name AS name,g.main_group_id AS mainGroupId,COALESCE(m.main_group_name,'') AS mainGroupName,g.is_late_fee_applicable AS lateFeeApplicable,g.session FROM concession_group_name g LEFT JOIN main_group_name m ON m.school_id=g.school_id AND m.main_group_id=g.main_group_id WHERE g.school_id=? AND g.session=? ORDER BY g.Group_name",school(a),ses);
    }

    @GetMapping("/concession-main-groups")
    public List<Map<String,Object>> concessionMainGroups(Authentication a){return jdbc.queryForList("SELECT main_group_id AS id,main_group_name AS name FROM main_group_name WHERE school_id=? AND IsActive=1 ORDER BY main_group_name",school(a));}

    @PostMapping("/concession-main-groups") @Transactional
    public Map<String,Object> addConcessionMainGroup(@RequestBody Map<String,Object>r,Authentication a){writeGuard.requireWritable();String sc=school(a);Integer next=jdbc.queryForObject("SELECT ISNULL(MAX(main_group_id),0)+1 FROM main_group_name WHERE school_id=?",Integer.class,sc);jdbc.update("INSERT INTO main_group_name(main_group_id,main_group_name,school_id,IsActive,created_by,created_date) VALUES(?,?,?,1,?,GETDATE())",next,s(r.get("name")),sc,staff(a));return row("message","Main group saved","id",next);}

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
        writeGuard.requireWritable();String sc=school(a),ses=s(r.get("session"));int installment=i(r.get("installmentNo")),month=i(r.get("monthId")),feeType=i(r.get("feeTypeId"));Object feeHead=r.get("feeHeadId");String desc=s(r.get("description")),code=s(r.get("code"));
        List<Integer> heads=new ArrayList<>();if(feeHead==null||s(feeHead).isBlank()||"null".equalsIgnoreCase(s(feeHead)))heads=jdbc.queryForList("SELECT fee_head_id FROM fee_head_master WHERE school_id=? AND session=? AND fee_type_id=? AND status=1 ORDER BY [order],fee_head_id",Integer.class,sc,ses,feeType);else heads.add(i(feeHead));if(heads.isEmpty())heads.add(0);
        if(heads.isEmpty())return row("message","No matching fee head","inserted",0);boolean typeWide=feeType==2||feeType==5||feeType==3;boolean typeExists=typeWide&&jdbc.queryForObject("SELECT CASE WHEN EXISTS(SELECT 1 FROM Installment_Master WHERE school_id=? AND session=? AND fee_type_id=?) THEN 1 ELSE 0 END",Integer.class,sc,ses,feeType)==1;int inserted=0;for(Integer head:heads){boolean exists=typeWide?typeExists:jdbc.queryForObject("SELECT CASE WHEN EXISTS(SELECT 1 FROM Installment_Master WHERE school_id=? AND session=? AND installment_no=? AND month_id=? AND fee_type_id=? AND fee_head_id=?) THEN 1 ELSE 0 END",Integer.class,sc,ses,installment,month,feeType,head)==1;if(exists)continue;jdbc.update("INSERT INTO Installment_Master(installment_no,fee_type_id,fee_head_id,month_id,due_date,status,created_date,created_by,school_id,session,installment_dis,inst_code,other_installment_dis) VALUES(?,?,?,?,?,1,GETDATE(),?,?,?,?,?,?)",installment,feeType,head,month,r.get("dueDate"),staff(a),sc,ses,desc,code,s(r.get("otherDescription")));inserted++;}return row("message","Installment saved","inserted",inserted);
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
    public Map<String,Object> saveStudentConcession(@RequestBody Map<String,Object>r,Authentication a){writeGuard.requireWritable();String sc=school(a),ses=s(r.get("session"));String studentId=s(r.get("studentId"));String type=s(r.get("type"));BigDecimal value=money(r.get("value"));Integer existing=jdbc.queryForObject("SELECT TOP 1 id FROM fee_concession WHERE school_id=? AND session=? AND stu_id=? AND inst_no=? AND fee_head_id=?",Integer.class,sc,ses,studentId,r.get("installmentNo"),r.get("feeHeadId"));if(existing!=null){jdbc.update("UPDATE fee_concession SET con_type=?,con_charge=?,con_per=?,remark=?,status=1,modified_date=GETDATE(),modified_by=? WHERE id=? AND school_id=?",type,"Per".equalsIgnoreCase(type)?BigDecimal.ZERO:value,"Per".equalsIgnoreCase(type)?value.toPlainString():"",s(r.get("remark")),staff(a),existing,sc);}else{jdbc.update("INSERT INTO fee_concession(inst_no,fee_head_id,fee_type_id,con_type,con_charge,con_per,stu_id,status,created_date,created_by,remark,school_id,session) VALUES(?,?,?,?,?,?,?,1,GETDATE(),?,?,?,?)",r.get("installmentNo"),r.get("feeHeadId"),r.get("feeTypeId"),type,"Per".equalsIgnoreCase(type)?BigDecimal.ZERO:value,"Per".equalsIgnoreCase(type)?value.toPlainString():"",studentId,staff(a),s(r.get("remark")),sc,ses);}return row("message","Student concession saved");}

    @PostMapping("/deposit") @Transactional
    public Map<String,Object> deposit(@RequestBody Map<String,Object>r,Authentication a){
        writeGuard.requireWritable();
        String sc=school(a),stf=staff(a),studentId=s(r.get("studentId")),ses=s(r.get("session"));
        if(ses.isBlank())ses=defaultSession(sc);
        if(studentId.isBlank())throw new IllegalArgumentException("Student is required");
        String payDate=s(r.get("paymentDate"));if(payDate.isBlank())payDate=LocalDate.now().toString();
        Map<String,Object> stu=jdbc.queryForMap("SELECT TOP 1 registration_no,uid FROM TB_StudentMaster WHERE school_id=? AND CAST(stu_id AS varchar(50))=?",sc,studentId);
        String adm=s(stu.get("registration_no")),feeType=s(r.get("feeType"));
        List<Map<String,Object>> dues=maps(r.get("dues"));
        if(dues.isEmpty())throw new IllegalArgumentException("Select at least one installment");
        List<Map<String,Object>> payments=maps(r.get("payments"));
        if(payments.isEmpty())payments.add(row("mop",s(r.get("mop")).isBlank()?"Cash":s(r.get("mop")),"bankId",s(r.get("bankId")),"transactionNo",s(r.get("transactionNo")),"transactionDate",s(r.get("transactionDate")),"amount",r.get("amount"),"useAmount",r.get("useAmount")));
        BigDecimal adjustment=money(r.get("advanceAdjusted")),remainingAdjustment=adjustment,total=BigDecimal.ZERO;
        if(adjustment.signum()>0){
            BigDecimal selectedTotal=BigDecimal.ZERO;
            for(Map<String,Object> due:dues){BigDecimal base=money(due.getOrDefault("baseAmount",due.get("balance")));BigDecimal late=truthy(due.get("waiveOff"))?BigDecimal.ZERO:money(due.get("lateFee"));selectedTotal=selectedTotal.add(base).add(late);}
            if(adjustment.compareTo(selectedTotal)>0)throw new IllegalArgumentException("Advance adjustment cannot exceed total payable");
            List<Map<String,Object>> advanceRows=jdbc.queryForList("SELECT TOP 1 af.uid,COALESCE(p.receipt_no,p.receiptno,'') AS receiptNo,COALESCE(p.mop,'Cash') AS mop,COALESCE(p.transaction_no,'') AS transactionNo,COALESCE(p.transaction_date,?) AS transactionDate FROM advance_fee af LEFT JOIN fee_payment_detail p ON p.school_id=af.school_id AND p.uid=af.uid WHERE af.school_id=? AND af.Admno=? AND af.status='active' AND COALESCE(af.balance,0)>=? ORDER BY af.created_date",payDate,sc,adm,adjustment);
            if(advanceRows.isEmpty())throw new IllegalArgumentException("Advance amount is no longer available");
            Map<String,Object> advanceRow=advanceRows.get(0);
            int changed=jdbc.update("UPDATE TOP (1) advance_fee SET advance_adjust=COALESCE(advance_adjust,0)+?,balance=COALESCE(balance,0)-?,status=CASE WHEN COALESCE(balance,0)-?<=0 THEN 'Inactive' ELSE status END WHERE school_id=? AND Admno=? AND status='active' AND COALESCE(balance,0)>=?",adjustment,adjustment,adjustment,sc,adm,adjustment);
            if(changed!=1)throw new IllegalArgumentException("Advance amount is no longer available");
            Map<String,Object> advancePayment=new LinkedHashMap<>();advancePayment.put("mop",s(advanceRow.get("mop")));advancePayment.put("bankId","");advancePayment.put("transactionNo",s(advanceRow.get("transactionNo")));advancePayment.put("transactionDate",s(advanceRow.get("transactionDate")));advancePayment.put("settlementDate","");advancePayment.put("amount",adjustment);advancePayment.put("useAmount",adjustment);advancePayment.put("advanceUid",s(advanceRow.get("uid")));advancePayment.put("receiptNo",s(advanceRow.get("receiptNo")));payments.add(0,advancePayment);
            remainingAdjustment=BigDecimal.ZERO;
        }
        List<Map<String,Object>> receipts=new ArrayList<>();
        int dueIndex=0;
        for(Map<String,Object> payment:payments){
            BigDecimal available=money(payment.get("useAmount"));if(available.signum()==0)available=money(payment.get("amount"));
            if(available.signum()<=0)continue;
            String advanceUid=s(payment.get("advanceUid"));boolean existingPayment=!advanceUid.isBlank();String paymentUid=existingPayment?advanceUid:UUID.randomUUID().toString().substring(0,8),receipt=s(payment.get("receiptNo"));if(receipt.isBlank())receipt=nextReceipt(sc,ses,feeType);
            String mop=s(payment.get("mop")).isBlank()?"Cash":s(payment.get("mop")),txn=s(payment.get("transactionNo")),txnDate=s(payment.get("transactionDate"));if(txnDate.isBlank())txnDate=payDate;
            BigDecimal paymentApplied=BigDecimal.ZERO;
            while(available.signum()>0 && dueIndex<dues.size()){
                Map<String,Object> selected=dues.get(dueIndex);BigDecimal baseDue=money(selected.getOrDefault("baseAmount",selected.get("balance")));BigDecimal lateDue=money(selected.get("lateFee"));if(truthy(selected.get("waiveOff")))lateDue=BigDecimal.ZERO;
                BigDecimal dueTotal=baseDue.add(lateDue);if(dueTotal.signum()<=0){dueIndex++;continue;}
                BigDecimal applied=available.min(dueTotal),basePaid=applied.min(baseDue),latePaid=applied.subtract(basePaid);String source=s(selected.get("source"));int id=i(selected.get("id"));
                if("previous".equalsIgnoreCase(source))applyPreviousDue(sc,studentId,adm,selected,id,basePaid,latePaid,paymentUid,payDate,mop,s(payment.get("bankId")),txn,receipt,stf);
                else applyCurrentDue(sc,studentId,selected,id,basePaid,latePaid,paymentUid,payDate,mop,s(payment.get("bankId")),txn,receipt,stf);
                available=available.subtract(applied);paymentApplied=paymentApplied.add(applied);total=total.add(applied);
                if(applied.compareTo(dueTotal)>=0)dueIndex++;
                else {selected.put("baseAmount",baseDue.subtract(basePaid));selected.put("balance",baseDue.subtract(basePaid));selected.put("lateFee",lateDue.subtract(latePaid));}
            }
            if(available.signum()>0){
                jdbc.update("INSERT INTO advance_fee(advance_amount,advance_adjust,balance,created_date,created_by,school_id,session,uid,status,advance_type,Admno) VALUES(?,?,?,?,?,?,?,?,?,?,?)",available,0,available,LocalDateTime.now(),stf,sc,ses,paymentUid,"active","Refund",adm);
                total=total.add(available);paymentApplied=paymentApplied.add(available);
            }
            if(paymentApplied.signum()>0){
                 if(existingPayment){receipts.add(row("receiptNo",receipt,"uid",paymentUid,"amount",paymentApplied,"mop",mop));continue;}
                jdbc.update("INSERT INTO fee_payment_detail(financial_year,school_id,admno,mop,fee_type,transaction_no,Bank_id,amount,paymentdate,receiptno,uid,status,deposit_by,deposit_date,transaction_date,student_unique_id,admission_no,created_by,created_on,fee_type_id,pay_date,pay_mode,receipt_no,session,stu_id,Settlement_Date) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",ses,sc,adm,mop,feeType,txn,s(payment.get("bankId")),paymentApplied.toPlainString(),payDate,receipt,paymentUid,"1",stf,payDate,txnDate,s(stu.get("uid")),adm,stf,LocalDateTime.now(),feeType,payDate,mop,receipt,ses,studentId,s(payment.get("settlementDate")));
                receipts.add(row("receiptNo",receipt,"uid",paymentUid,"amount",paymentApplied,"mop",mop));
            }
        }
        if(remainingAdjustment.signum()>0){
            int changed=jdbc.update("UPDATE TOP (1) advance_fee SET advance_adjust=COALESCE(advance_adjust,0)+?,balance=CASE WHEN COALESCE(balance,0)>=? THEN balance-? ELSE 0 END,status=CASE WHEN COALESCE(balance,0)<=? THEN 'Inactive' ELSE status END WHERE school_id=? AND Admno=? AND status='active' AND COALESCE(balance,0)>0",remainingAdjustment,remainingAdjustment,remainingAdjustment,remainingAdjustment,sc,adm);
            if(changed==0)throw new IllegalArgumentException("Advance amount is no longer available");
        }
        if(total.signum()<=0)throw new IllegalArgumentException("Payment amount is required");
        return row("message","Fee deposited successfully","receiptNo",receipts.isEmpty()?nextReceipt(sc,ses,feeType):receipts.get(receipts.size()-1).get("receiptNo"),"receipts",receipts,"amount",total);
    }

    @GetMapping("/students/{studentId}/receivable")
    public Map<String,Object> receivable(@PathVariable String studentId,@RequestParam(required=false)String session,Authentication a){
        String sc=school(a),ses=s(session);if(ses.isBlank())ses=defaultSession(sc);Map<String,Object> st=jdbc.queryForMap("SELECT TOP 1 registration_no FROM TB_StudentMaster WHERE school_id=? AND CAST(stu_id AS varchar(50))=?",sc,studentId);String adm=s(st.get("registration_no"));
        return row("opening",jdbc.queryForList("SELECT TOP 1 advance_amount AS opening,advance_adjust AS adjust,balance,CONVERT(varchar(100),created_date,106) AS createdDate FROM advance_fee WHERE school_id=? AND advance_type='Receivable' AND Admno=? AND session=? ORDER BY created_date DESC",sc,adm,ses),"rows",jdbc.queryForList("SELECT STRING_AGG(CAST(inst_no AS varchar(20)),',') WITHIN GROUP (ORDER BY inst_no) AS installmentNo,uid,CONVERT(varchar(100),deposit_date,106) AS depositDate,receipt_no AS receiptNo,SUM(receive_amt) AS receiveAmount FROM stu_headwise_deposit WHERE school_id=? AND status='active' AND advance_fee_id IN (SELECT CAST(advance_fee_id AS varchar(100)) FROM advance_fee WHERE school_id=? AND advance_type='Receivable' AND Admno=? AND session=?) GROUP BY uid,deposit_date,receipt_no ORDER BY depositDate DESC",sc,sc,adm,ses));
    }

    @GetMapping("/optional")
    public List<Map<String,Object>> optional(@RequestParam String session,@RequestParam int feeHeadId,@RequestParam(required=false)Integer classId,@RequestParam(required=false)String admissionNo,Authentication a){
        if(classId==null||classId<=0)throw new IllegalArgumentException("Select class first");
        return optionalFeeService.load(school(a), session, feeHeadId, classId, admissionNo);
    }

    @PostMapping("/optional") @Transactional
    public Map<String,Object> saveOptional(@RequestBody Map<String,Object>r,Authentication a){
        return optionalFeeService.save(school(a), staff(a), s(r.get("session")), i(r.get("feeHeadId")), maps(r.get("students")));
    }

    @PostMapping("/heads") public Map<String,Object> addHead(@RequestBody Map<String,Object>r,Authentication a){writeGuard.requireWritable();String sc=school(a),ses=s(r.get("session"));jdbc.update("INSERT INTO fee_head_master(fee_head_id,fee_type_id,fee_head,fee_code,[order],status,created_date,created_by,school_id,session,isOptional) VALUES((SELECT ISNULL(MAX(fee_head_id),0)+1 FROM fee_head_master WHERE school_id=?),?,?,?,?,1,GETDATE(),?,?,?,?)",sc,r.get("feeTypeId"),s(r.get("name")),s(r.get("feeCode")),r.get("sortOrder"),sc,ses,r.get("isOptional"));return row("message","Fee head saved");}
    @PutMapping("/heads/{id}") public Map<String,Object> updateHead(@PathVariable int id,@RequestBody Map<String,Object>r,Authentication a){writeGuard.requireWritable();jdbc.update("UPDATE fee_head_master SET fee_head=?,fee_code=?,fee_type_id=?,[order]=?,status=?,isOptional=?,modified_date=GETDATE(),modified_by=? WHERE row_id=? AND school_id=?",s(r.get("name")),s(r.get("feeCode")),r.get("feeTypeId"),r.get("sortOrder"),r.get("status"),r.get("isOptional"),staff(a),id,school(a));return row("message","Fee head updated");}
    @DeleteMapping("/heads/{id}") public Map<String,Object> deleteHead(@PathVariable int id,Authentication a){writeGuard.requireWritable();jdbc.update("UPDATE fee_head_master SET status=0,modified_date=GETDATE(),modified_by=? WHERE row_id=? AND school_id=?",staff(a),id,school(a));return row("message","Fee head deactivated");}

    @SuppressWarnings("unchecked")
    private static List<Map<String,Object>> maps(Object raw){
        if(!(raw instanceof List<?> list))return new ArrayList<>();
        List<Map<String,Object>> result=new ArrayList<>();
        for(Object value:list)if(value instanceof Map<?,?> m){Map<String,Object> copy=new LinkedHashMap<>();m.forEach((k,v)->copy.put(String.valueOf(k),v));result.add(copy);}
        return result;
    }
    private static boolean truthy(Object value){String v=s(value);return "1".equals(v)||"true".equalsIgnoreCase(v)||"yes".equalsIgnoreCase(v);}
    private static List<Integer> parseIds(Object value){List<Integer> ids=new ArrayList<>();for(String part:s(value).split(",")){if(!part.isBlank())try{ids.add(Integer.parseInt(part.trim()));}catch(NumberFormatException ignored){}}return ids;}

    private BigDecimal calculateLateFee(String sc,String ses,String studentId,int installment,int feeType,BigDecimal balance,String admissionDate){
        if(balance.signum()<=0||feeType==6||feeType==3)return BigDecimal.ZERO;
        Integer paidLate=jdbc.queryForObject("SELECT COUNT(1) FROM fee_installmentwise_deposit WHERE school_id=? AND session=? AND stu_id=? AND installment_no=? AND status='active' AND TRY_CONVERT(decimal(18,2),inst_late_fee)>0",Integer.class,sc,ses,studentId,installment);if(paidLate!=null&&paidLate>0)return BigDecimal.ZERO;
        List<Map<String,Object>> rules=jdbc.queryForList("SELECT from_date AS fromDate,to_date AS toDate,mode,charge FROM late_fee WHERE school_id=? AND session=? AND inst_no=? AND fee_type_id=? AND status=1 ORDER BY from_date",sc,ses,installment,feeType);if(rules.isEmpty()&&feeType!=0)rules=jdbc.queryForList("SELECT from_date AS fromDate,to_date AS toDate,mode,charge FROM late_fee WHERE school_id=? AND session=? AND inst_no=? AND fee_type_id=0 AND status=1 ORDER BY from_date",sc,ses,installment);
        if(rules.isEmpty())return BigDecimal.ZERO;LocalDate today=LocalDate.now(),registered=parseDate(admissionDate);String mode=s(rules.get(0).get("mode"));Map<String,Object> chosen=rules.get(rules.size()-1);for(Map<String,Object> rule:rules){LocalDate from=parseDate(s(rule.get("fromDate"))),to=parseDate(s(rule.get("toDate")));if((!today.isBefore(from)&&!today.isAfter(to))){chosen=rule;break;}}
        LocalDate from=parseDate(s(chosen.get("fromDate")));if(registered!=null&&registered.isAfter(from))from=registered;if(today.isBefore(from))return BigDecimal.ZERO;BigDecimal charge=money(chosen.get("charge"));if("Per Day".equalsIgnoreCase(mode))return charge.multiply(BigDecimal.valueOf(java.time.temporal.ChronoUnit.DAYS.between(from,today)));if("Monthly".equalsIgnoreCase(mode))return charge.add(charge.multiply(BigDecimal.valueOf(java.time.temporal.ChronoUnit.MONTHS.between(from,today))));if("Weekly".equalsIgnoreCase(mode))return charge.multiply(BigDecimal.valueOf(java.time.temporal.ChronoUnit.WEEKS.between(from,today)));if("Percentage".equalsIgnoreCase(mode))return balance.multiply(charge).divide(BigDecimal.valueOf(100));return charge;
    }
    private static LocalDate parseDate(String value){if(value==null||value.isBlank())return LocalDate.MIN;String v=value.trim();if(v.length()>10)v=v.substring(0,10);try{return LocalDate.parse(v);}catch(Exception ignored){try{return java.sql.Date.valueOf(v).toLocalDate();}catch(Exception ignoredAgain){return LocalDate.MIN;}}}

    private void applyCurrentDue(String sc,String studentId,Map<String,Object> selected,int id,BigDecimal basePaid,BigDecimal latePaid,String uid,String payDate,String mop,String bankId,String txn,String receipt,String staffId){
        List<Integer> ids=parseIds(selected.get("lineIds"));if(ids.size()>1){BigDecimal left=basePaid;for(int n=0;n<ids.size()&&left.signum()>0;n++){List<Map<String,Object>> r=jdbc.queryForList("SELECT TOP 1 balance FROM fee_student_ledger WHERE id=? AND school_id=?",ids.get(n),sc);if(r.isEmpty())continue;BigDecimal paid=left.min(money(r.get(0).get("balance")));Map<String,Object> line=new LinkedHashMap<>(selected);line.remove("lineIds");applyCurrentDueLine(sc,studentId,line,ids.get(n),paid,n==0?latePaid:BigDecimal.ZERO,uid,payDate,mop,bankId,txn,receipt,staffId);left=left.subtract(paid);}return;}applyCurrentDueLine(sc,studentId,selected,id,basePaid,latePaid,uid,payDate,mop,bankId,txn,receipt,staffId);
    }

    private void applyCurrentDueLine(String sc,String studentId,Map<String,Object> selected,int id,BigDecimal basePaid,BigDecimal latePaid,String uid,String payDate,String mop,String bankId,String txn,String receipt,String staffId){
        List<Map<String,Object>> rows=jdbc.queryForList("SELECT TOP 1 l.session,l.installment_no,l.fee_head_id,l.fee_type_id,l.payable,l.balance,COALESCE(h.fee_head,'') AS feeHead FROM fee_student_ledger l LEFT JOIN fee_head_master h ON h.school_id=l.school_id AND h.session=l.session AND h.fee_head_id=l.fee_head_id WHERE l.id=? AND l.school_id=? AND COALESCE(l.balance,0)>0",id,sc);
        if(rows.isEmpty())throw new IllegalArgumentException("Fee due changed; reload student dues");
        Map<String,Object>d=rows.get(0);String inst=s(d.get("installment_no"));int feeHead=i(d.get("fee_head_id")),feeType=i(d.get("fee_type_id"));
        if(basePaid.signum()>0){int changed=jdbc.update("UPDATE fee_student_ledger SET receive_amount=COALESCE(receive_amount,0)+?,balance=CASE WHEN COALESCE(balance,0)>=? THEN balance-? ELSE 0 END WHERE id=? AND school_id=? AND COALESCE(balance,0)>=?",basePaid,basePaid,basePaid,id,sc,basePaid);if(changed!=1)throw new IllegalArgumentException("Fee due changed; reload student dues");jdbc.update("INSERT INTO stu_headwise_deposit(stu_id,inst_no,fee_head_id,fee_head_name,payable,receive_amt,uid,deposit_date,school_id,session,fee_type_id,receipt_no,status) SELECT ?,?,?,?,?,?, ?,GETDATE(),?,?,?,?,'active'",studentId,inst,feeHead,s(d.get("feeHead")),money(d.get("payable")),basePaid,uid,sc,s(selected.getOrDefault("session",selected.get("sourceSession"))),feeType,receipt);}
        if(basePaid.signum()>0||latePaid.signum()>0){jdbc.update("INSERT INTO fee_installmentwise_deposit(stu_id,installment_no,deposit_amount,inst_late_fee,uid,created_date,created_by,session,school_id,status) VALUES(?,?,?,?,?,GETDATE(),?,?,?,'active')",studentId,inst,basePaid.toPlainString(),latePaid.toPlainString(),uid,staffId,s(d.get("session")),sc);jdbc.update("INSERT INTO stu_fee_deposit(stu_id,inst_no,pay_mode,bank_id,pay_status,pay_date,dip_amt,created_date,created_by,school_id,session,uid,transaction_no,transaction_date,late_fee,fee_type,receipt_no,advance_paid,isVerified,transaction_amount,status) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",studentId,inst,mop,bankId,"Success",payDate,basePaid.add(latePaid),LocalDateTime.now(),staffId,sc,s(d.get("session")),uid,txn,payDate,latePaid,feeType,receipt,0,true,basePaid.add(latePaid).toPlainString(),"1");}
    }

    private void applyPreviousDue(String sc,String studentId,String adm,Map<String,Object> selected,int id,BigDecimal basePaid,BigDecimal latePaid,String uid,String payDate,String mop,String bankId,String txn,String receipt,String staffId){
        List<Map<String,Object>> rows=jdbc.queryForList("SELECT TOP 1 p.session,p.inst_no,p.fee_head,p.payable,p.balance,COALESCE(h.fee_head_id,0) AS feeHeadId,COALESCE(h.fee_type_id,0) AS feeTypeId FROM fee_previousdues p LEFT JOIN fee_head_master h ON h.school_id=p.school_id AND h.session=p.session AND h.fee_head=p.fee_head WHERE p.id=? AND p.school_id=?",id,sc);if(rows.isEmpty())throw new IllegalArgumentException("Previous fee due changed; reload student dues");Map<String,Object>d=rows.get(0);String prevSession=s(d.get("session")),inst=s(d.get("inst_no"));int feeHead=i(d.get("feeHeadId")),feeType=i(d.get("feeTypeId"));
        if(basePaid.signum()>0){int changed=feeHead>0?jdbc.update("UPDATE fee_student_ledger SET receive_amount=COALESCE(receive_amount,0)+?,balance=CASE WHEN COALESCE(balance,0)>=? THEN balance-? ELSE 0 END WHERE school_id=? AND session=? AND stu_id=? AND installment_no=? AND fee_head_id=? AND COALESCE(balance,0)>=?",basePaid,basePaid,basePaid,sc,prevSession,studentId,inst,feeHead,basePaid):0;if(changed==0)jdbc.update("UPDATE fee_previousdues SET receive_amount=COALESCE(receive_amount,0)+?,balance=CASE WHEN COALESCE(balance,0)>=? THEN balance-? ELSE 0 END WHERE id=? AND school_id=? AND COALESCE(balance,0)>=?",basePaid,basePaid,basePaid,id,sc,basePaid);if(feeHead>0)jdbc.update("INSERT INTO stu_headwise_deposit(stu_id,inst_no,fee_head_id,fee_head_name,payable,receive_amt,uid,deposit_date,school_id,session,fee_type_id,receipt_no,status) VALUES(?,?,?,?,?,?,?,GETDATE(),?,?,?,?,'active')",studentId,inst,feeHead,s(d.get("fee_head")),money(d.get("payable")),basePaid,uid,sc,prevSession,feeType,receipt);}
        if(basePaid.signum()>0||latePaid.signum()>0)jdbc.update("INSERT INTO stu_fee_deposit(stu_id,inst_no,pay_mode,bank_id,pay_status,pay_date,dip_amt,created_date,created_by,school_id,session,uid,transaction_no,transaction_date,late_fee,fee_type,receipt_no,advance_paid,isVerified,transaction_amount,status) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",studentId,inst,mop,bankId,"Success",payDate,basePaid.add(latePaid),LocalDateTime.now(),staffId,sc,prevSession,uid,txn,payDate,latePaid,feeType,receipt,0,true,basePaid.add(latePaid).toPlainString(),"1");
    }

    private String defaultSession(String sc){List<Map<String,Object>>r=jdbc.queryForList("SELECT TOP 1 session FROM TB_Session WHERE school_id=? ORDER BY CASE WHEN status='Default' THEN 0 ELSE 1 END,id DESC",sc);return r.isEmpty()?"":s(r.get(0).get("session"));}
    private static String previousSession(String session){
        String compact=s(session).replace(" ","");String[] parts=compact.split("-");
        if(parts.length!=2) return "";
        try { int start=Integer.parseInt(parts[0]),end=Integer.parseInt(parts[1]); return (start-1)+" - "+start; }
        catch(NumberFormatException ignored){ return ""; }
    }
    private String nextReceipt(String sc,String ses){return nextReceipt(sc,ses,"");}
    private String nextReceipt(String sc,String ses,String feeType){
        List<Map<String,Object>> cfg=jdbc.queryForList("SELECT TOP 1 ReceiptFeeTypeSeriesMode,ResetReceiptSequenceEachFY FROM configuration WHERE school_id=?",sc);boolean byType=!cfg.isEmpty()&&truthy(cfg.get(0).get("ReceiptFeeTypeSeriesMode")),byYear=!cfg.isEmpty()&&truthy(cfg.get(0).get("ResetReceiptSequenceEachFY"));StringBuilder q=new StringBuilder("SELECT ISNULL(MAX(TRY_CONVERT(BIGINT,receiptno)),0)+1 FROM fee_payment_detail WHERE school_id=? AND status='Active'");List<Object>p=new ArrayList<>(List.of(sc));if(byYear){q.append(" AND financial_year=?");p.add(ses);}if(byType){q.append(" AND fee_type=?");p.add(feeType);}Long n=jdbc.queryForObject(q.toString(),Long.class,p.toArray());return String.valueOf(n==null?1:n);
    }
}
