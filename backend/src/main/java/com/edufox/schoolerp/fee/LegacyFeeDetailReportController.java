package com.edufox.schoolerp.fee;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * Direct-SQL implementations of the remaining legacy Fee report pages.
 * The old pages used procedures only as a data access mechanism; these
 * endpoints keep the old table/column contract and reproduce the page flow
 * without executing a procedure.
 */
@RestController
@RequestMapping("/api/fees/legacy-reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class LegacyFeeDetailReportController {
    private final JdbcTemplate jdbc;

    private static Claims claims(Authentication a) { return (Claims) a.getDetails(); }
    private static String school(Authentication a) { return text(claims(a).get("schoolId", String.class)); }
    private static String text(Object value) { return value == null ? "" : String.valueOf(value).trim(); }
    private static int integer(Object value) { try { return Integer.parseInt(text(value)); } catch (Exception ignored) { return 0; } }

    private String session(Object requested, String schoolId) {
        if (!text(requested).isBlank()) return text(requested);
        List<Map<String, Object>> rows = jdbc.queryForList(
            "SELECT TOP 1 session FROM TB_Session WHERE school_id=? ORDER BY CASE WHEN status='Default' THEN 0 ELSE 1 END,id DESC", schoolId);
        return rows.isEmpty() ? "" : text(rows.get(0).get("session"));
    }

    @GetMapping("/detail/bootstrap")
    public Map<String, Object> bootstrap(@RequestParam(required = false) String session, Authentication authentication) {
        String sc = school(authentication), ses = session(session, sc);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("session", ses);
        result.put("sessions", jdbc.queryForList("SELECT id,session AS value,session AS label,status FROM TB_Session WHERE school_id=? ORDER BY id DESC", sc));
        result.put("classes", jdbc.queryForList("SELECT c.sec_id AS id,c.class_id AS classId,c.section,cn.class_name AS className,cn.class_name+' ('+c.section+')' AS label FROM TB_Class c JOIN class_name cn ON cn.school_id=c.school_id AND cn.id=c.class_id WHERE c.school_id=? ORDER BY c.class_id,c.section", sc));
        result.put("installments", jdbc.queryForList("SELECT DISTINCT installment_no AS value,installment_no AS label FROM Installment_Master WHERE school_id=? AND session=? AND status=1 ORDER BY installment_no", sc, ses));
        result.put("school", jdbc.queryForList("SELECT TOP 1 schoolname,logo_path,web_address FROM schoolmaster WHERE school_id=?", sc));
        return result;
    }

    @GetMapping("/student-search")
    public List<Map<String, Object>> studentSearch(@RequestParam(required = false, defaultValue = "") String query,
                                                   @RequestParam(required = false) String session,
                                                   Authentication authentication) {
        String sc = school(authentication), ses = session(session, sc), value = "%" + text(query) + "%";
        return jdbc.queryForList("SELECT TOP 20 sm.registration_no AS admissionNo,UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'')))) AS studentName,COALESCE(cn.class_name,'')+' ('+COALESCE(tc.section,'')+')' AS className,COALESCE(so.father_name,'') AS fatherName FROM TB_StudentMaster sm JOIN student_other_info so ON so.school_id=sm.school_id AND so.registration_no=sm.registration_no AND so.uid=sm.uid LEFT JOIN class_name cn ON cn.school_id=sm.school_id AND cn.id=sm.class_id LEFT JOIN TB_Class tc ON tc.school_id=sm.school_id AND tc.sec_id=sm.sec_id WHERE sm.school_id=? AND sm.session=? AND LOWER(sm.status)='registered' AND (sm.registration_no LIKE ? OR so.first_name LIKE ? OR so.last_name LIKE ? OR so.father_name LIKE ?) ORDER BY so.first_name,so.last_name", sc, ses, value, value, value, value);
    }

    @GetMapping("/student-summary")
    public Map<String, Object> studentSummary(@RequestParam(required = false) String session,
                                               @RequestParam String admissionNo,
                                               Authentication authentication) {
        String sc = school(authentication), ses = session(session, sc);
        Map<String, Object> result = new LinkedHashMap<>();
        List<Map<String, Object>> students = jdbc.queryForList("SELECT TOP 1 sm.stu_id AS studentId,sm.registration_no AS registrationNo,sm.FeeBookNo AS feeBookNo,sm.class_id AS classId,sm.sec_id AS secId,UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'')))) AS studentName,COALESCE(so.father_name,'') AS fatherName,COALESCE(so.mobile,'') AS mobile,COALESCE(so.phone,'') AS phone,COALESCE(so.gender,'') AS gender,COALESCE(so.DOB,'') AS dob,COALESCE(so.childcat,'') AS category,COALESCE(so.studentimage,'') AS studentImage,COALESCE(cn.class_name,'') AS className,COALESCE(tc.section,'') AS section FROM TB_StudentMaster sm JOIN student_other_info so ON so.school_id=sm.school_id AND so.registration_no=sm.registration_no AND so.uid=sm.uid LEFT JOIN class_name cn ON cn.school_id=sm.school_id AND cn.id=sm.class_id LEFT JOIN TB_Class tc ON tc.school_id=sm.school_id AND tc.sec_id=sm.sec_id WHERE sm.school_id=? AND sm.session=? AND sm.registration_no=?", sc, ses, text(admissionNo));
        if (students.isEmpty()) return Map.of("session", ses, "student", null, "feeRows", List.of(), "transactions", List.of());
        Map<String, Object> student = students.get(0);
        int studentId = integer(student.get("studentId")), classId = integer(student.get("classId"));
        List<Map<String, Object>> feeRows = summaryRows(sc, ses, studentId, classId);
        List<Map<String, Object>> transactions = jdbc.queryForList("SELECT MIN(s.id) AS id,s.inst_no AS inst_no,s.receipt_no AS receipt_no,CONVERT(varchar(20),MAX(s.deposit_date),103) AS paymentdate,COALESCE(MAX(p.mop),'') AS mop,COALESCE(MAX(p.transaction_no),'') AS transaction_no,SUM(COALESCE(s.receive_amt,0)) AS installment_amount,COALESCE(MAX(fd.late_fee),0) AS latefee,COALESCE(MAX(fd.advance_paid),0) AS advance_amount,COALESCE(MAX(p.amount),SUM(COALESCE(s.receive_amt,0))) AS amount,? AS session,COALESCE(MAX(p.reconsil_BY),'') AS reconsil_BY FROM stu_headwise_deposit s LEFT JOIN (SELECT school_id,stu_id,inst_no,receipt_no,MAX(late_fee) AS late_fee,MAX(advance_paid) AS advance_paid FROM stu_fee_deposit GROUP BY school_id,stu_id,inst_no,receipt_no) fd ON fd.school_id=s.school_id AND fd.stu_id=s.stu_id AND fd.inst_no=s.inst_no AND fd.receipt_no=s.receipt_no LEFT JOIN fee_payment_detail p ON p.school_id=s.school_id AND p.receiptno=s.receipt_no AND p.status='Active' WHERE s.school_id=? AND s.session=? AND s.stu_id=? AND LOWER(s.status)='active' GROUP BY s.inst_no,s.receipt_no ORDER BY MIN(s.id)", ses, sc, ses, studentId);
        result.put("session", ses); result.put("student", student); result.put("feeRows", feeRows); result.put("transactions", transactions);
        return result;
    }

    private List<Map<String, Object>> summaryRows(String sc, String ses, int studentId, int classId) {
        String sql = "WITH I AS (SELECT installment_no,MAX(COALESCE(installment_dis,'')) AS installment_dis,MAX(COALESCE(inst_code,'')) AS inst_code,MAX(due_date) AS due_date FROM Installment_Master WHERE school_id=? AND session=? AND status=1 GROUP BY installment_no), H AS (SELECT fee_head_id,fee_head,fee_type_id,isOptional FROM fee_head_master WHERE school_id=? AND session=? AND status=1 AND fee_type_id NOT IN ('6','7','12')), C AS (SELECT fee_head_id,fee_type_id,MAX(COALESCE(fee_charges,0)) AS fee_charges FROM fee_head_charge WHERE school_id=? AND session=? AND class_id=? AND status=1 GROUP BY fee_head_id,fee_type_id), X AS (SELECT i.installment_no,i.installment_dis,i.inst_code,i.due_date,h.fee_head_id,h.fee_type_id,COALESCE(c.fee_charges,0) AS charge FROM I i CROSS JOIN H h LEFT JOIN C c ON c.fee_head_id=h.fee_head_id AND c.fee_type_id=h.fee_type_id WHERE (h.fee_type_id='3' AND i.installment_no=1) OR EXISTS (SELECT 1 FROM Assign_Optional_Fee ao WHERE ao.school_id=? AND ao.session=? AND ao.stu_id=? AND ao.fee_head_id=h.fee_head_id AND ao.fee_type_id=h.fee_type_id AND ao.installment_no=i.installment_no AND (ao.Paid_status=0 OR ao.Paid_status IS NULL))), D AS (SELECT installment_no,SUM(TRY_CONVERT(float,deposit_amount)) AS received,MAX(TRY_CONVERT(float,inst_late_fee)) AS latefee FROM fee_installmentwise_deposit WHERE school_id=? AND session=? AND stu_id=? AND LOWER(status)='active' GROUP BY installment_no), W AS (SELECT installment_no,SUM(COALESCE(WaveOffFeeValue,0)) AS waveoff FROM WaveOffLateFees WHERE school_id=? AND session=? AND stu_id=? AND status=1 GROUP BY installment_no) SELECT x.installment_no AS installment_no,'Current Year' AS type,x.installment_dis,x.inst_code,x.due_date AS due_date,SUM(x.charge) AS InstallmentAmt,CAST(0 AS float) AS Concession,SUM(x.charge) AS Payable,CASE WHEN SUM(x.charge)-COALESCE(MAX(d.received),0)-COALESCE(MAX(w.waveoff),0)>0 AND CAST(GETDATE() AS date)>x.due_date THEN COALESCE((SELECT TOP 1 charge FROM late_fee lf WHERE lf.school_id=? AND lf.session=? AND lf.inst_no=x.installment_no AND lf.fee_type_id=0 AND lf.status=1 AND CAST(GETDATE() AS date) BETWEEN lf.from_date AND lf.to_date),0) ELSE COALESCE(MAX(d.latefee),0) END AS latefee,COALESCE(MAX(d.received),0) AS ReceiveAmt,CASE WHEN SUM(x.charge)-COALESCE(MAX(d.received),0)-COALESCE(MAX(w.waveoff),0)>0 THEN SUM(x.charge)-COALESCE(MAX(d.received),0)-COALESCE(MAX(w.waveoff),0)+CASE WHEN CAST(GETDATE() AS date)>x.due_date THEN COALESCE((SELECT TOP 1 charge FROM late_fee lf WHERE lf.school_id=? AND lf.session=? AND lf.inst_no=x.installment_no AND lf.fee_type_id=0 AND lf.status=1 AND CAST(GETDATE() AS date) BETWEEN lf.from_date AND lf.to_date),0) ELSE 0 END ELSE 0 END AS Balance_with_latefee,CONVERT(varchar(20),x.due_date,106) AS nextDueDate,CASE WHEN SUM(x.charge)-COALESCE(MAX(d.received),0)-COALESCE(MAX(w.waveoff),0)<=0 THEN 'Paid' ELSE 'Due' END AS status FROM X x LEFT JOIN D d ON d.installment_no=x.installment_no LEFT JOIN W w ON w.installment_no=x.installment_no GROUP BY x.installment_no,x.installment_dis,x.inst_code,x.due_date ORDER BY x.installment_no";
        List<Object> p = new ArrayList<>(List.of(sc,ses,sc,ses,sc,ses,classId,sc,ses,studentId,sc,ses,studentId,sc,ses,studentId,sc,ses,sc,ses));
        return jdbc.queryForList(sql, p.toArray());
    }

    @GetMapping("/installment-defaulter")
    public List<Map<String, Object>> installmentDefaulter(@RequestParam(required = false) String session,
                                                          @RequestParam int installmentNo,
                                                          @RequestParam(defaultValue = "Regular") String type,
                                                          Authentication authentication) {
        String sc = school(authentication), ses = session(session, sc);
        int feeType = "Transport".equalsIgnoreCase(type) ? 6 : 0;
        String typeFilter = feeType == 6 ? " AND h.fee_type_id='6'" : " AND h.fee_type_id NOT IN ('6','7','12')";
        String ledgerType = feeType == 6 ? " AND l.fee_type_id='6'" : " AND l.fee_type_id!='6'";
        String sql = "WITH A AS (SELECT stu_id,SUM(payable) AS payable,SUM(receive_amount) AS receive_amount FROM fee_student_ledger l WHERE l.school_id=? AND l.session=? AND l.installment_no<=?" + ledgerType + " GROUP BY stu_id), B AS (SELECT a.*,sm.registration_no AS adm_no,UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'')))) AS name,COALESCE(cn.class_name,'')+' ('+COALESCE(tc.section,'')+')' AS class_name,COALESCE(so.father_name,'') AS father_name,COALESCE(so.mobile,'') AS mobile_no FROM A a JOIN TB_StudentMaster sm ON sm.school_id=? AND sm.session=? AND sm.stu_id=a.stu_id JOIN student_other_info so ON so.school_id=sm.school_id AND so.registration_no=sm.registration_no AND so.uid=sm.uid LEFT JOIN class_name cn ON cn.school_id=sm.school_id AND cn.id=sm.class_id LEFT JOIN TB_Class tc ON tc.school_id=sm.school_id AND tc.sec_id=sm.sec_id WHERE a.receive_amount=0 AND a.payable<>0) SELECT b.adm_no,b.class_name AS class,b.name,b.father_name,b.mobile_no,b.stu_id,b.payable,b.receive_amount,STUFF((SELECT DISTINCT ','+CONVERT(varchar(20),l2.installment_no) FROM fee_student_ledger l2 WHERE l2.stu_id=b.stu_id AND l2.school_id=? AND l2.session=? AND l2.installment_no<=?" + ledgerType.replace("l.", "l2.") + " FOR XML PATH(''),TYPE).value('.','nvarchar(max)'),1,1,'') AS ins FROM B b ORDER BY b.name";
        List<Object> p = new ArrayList<>(List.of(sc,ses,installmentNo));
        if (feeType == 6) { /* the type predicate above has no placeholder */ }
        p.addAll(List.of(sc,ses,sc,ses,installmentNo));
        return jdbc.queryForList(sql, p.toArray());
    }

    @GetMapping("/headwise-defaulter")
    public List<Map<String, Object>> headwiseDefaulter(@RequestParam(required = false) String session,
                                                       @RequestParam int installmentNo,
                                                       @RequestParam(defaultValue = "0") int classId,
                                                       @RequestParam(defaultValue = "0") int sectionId,
                                                       @RequestParam(defaultValue = "3") int reportType,
                                                       @RequestParam(defaultValue = "0") int feeTypeId,
                                                       Authentication authentication) {
        String sc = school(authentication), ses = session(session, sc);
        String typeFilter = feeTypeId == 6 || feeTypeId == 7 ? " AND h.fee_type_id=?" : " AND h.fee_type_id NOT IN ('6','7','12')";
        StringBuilder sql = new StringBuilder("WITH B AS (SELECT sm.stu_id,sm.registration_no AS adm_no,sm.class_id,sm.sec_id,UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'')))) AS name,COALESCE(so.father_name,'') AS father_name,COALESCE(so.mobile,'') AS mobile_no,COALESCE(cn.class_name,'')+' ('+COALESCE(tc.section,'')+')' AS class_name FROM TB_StudentMaster sm JOIN student_other_info so ON so.school_id=sm.school_id AND so.registration_no=sm.registration_no AND so.uid=sm.uid LEFT JOIN class_name cn ON cn.school_id=sm.school_id AND cn.id=sm.class_id LEFT JOIN TB_Class tc ON tc.school_id=sm.school_id AND tc.sec_id=sm.sec_id WHERE sm.school_id=? AND sm.session=? AND LOWER(sm.status)='registered'), I AS (SELECT DISTINCT installment_no FROM Installment_Master WHERE school_id=? AND session=? AND status=1 AND installment_no=?), H AS (SELECT fee_head_id,fee_head,fee_type_id FROM fee_head_master h WHERE h.school_id=? AND h.session=? AND h.status=1" + typeFilter + "), C AS (SELECT fee_head_id,fee_type_id,class_id,MAX(COALESCE(fee_charges,0)) AS charge FROM fee_head_charge WHERE school_id=? AND session=? AND status=1 GROUP BY fee_head_id,fee_type_id,class_id), D AS (SELECT stu_id,TRY_CONVERT(int,inst_no) AS installment_no,fee_head_id,SUM(COALESCE(receive_amt,0)) AS received FROM stu_headwise_deposit WHERE school_id=? AND session=? AND LOWER(status)='active' GROUP BY stu_id,TRY_CONVERT(int,inst_no),fee_head_id) SELECT b.adm_no,b.name,b.class_name AS class,b.father_name,b.mobile_no,i.installment_no,h.fee_head, h.fee_type_id,COALESCE(c.charge,0) AS total_fee,COALESCE(d.received,0) AS total_paid,COALESCE(c.charge,0)-COALESCE(d.received,0) AS total_due FROM B b CROSS JOIN I i CROSS JOIN H h LEFT JOIN C c ON c.fee_head_id=h.fee_head_id AND c.fee_type_id=h.fee_type_id AND c.class_id=b.class_id LEFT JOIN D d ON d.stu_id=b.stu_id AND d.installment_no=i.installment_no AND d.fee_head_id=h.fee_head_id WHERE ((h.fee_type_id='3' AND i.installment_no=1) OR EXISTS (SELECT 1 FROM Assign_Optional_Fee ao WHERE ao.school_id=? AND ao.session=? AND ao.stu_id=b.stu_id AND ao.fee_head_id=h.fee_head_id AND ao.fee_type_id=h.fee_type_id AND ao.installment_no=i.installment_no AND (ao.Paid_status=0 OR ao.Paid_status IS NULL)))");
        List<Object> p = new ArrayList<>(List.of(sc,ses,sc,ses,installmentNo,sc,ses));
        if (feeTypeId == 6 || feeTypeId == 7) p.add(feeTypeId);
        p.addAll(List.of(sc,ses,sc,ses,sc,ses));
        if (classId != 0) { sql.append(" AND b.class_id=?"); p.add(classId); }
        if (sectionId != 0) { sql.append(" AND b.sec_id=?"); p.add(sectionId); }
        if (reportType == 1) sql.append(" AND COALESCE(d.received,0)>0");
        if (reportType == 3 || reportType == 44) sql.append(" AND COALESCE(c.charge,0)-COALESCE(d.received,0)>0");
        sql.append(" ORDER BY b.name,h.fee_head");
        return aggregateHeadwise(jdbc.queryForList(sql.toString(), p.toArray()));
    }

    private List<Map<String, Object>> aggregateHeadwise(List<Map<String, Object>> raw) {
        Map<String, Map<String, Object>> grouped = new LinkedHashMap<>();
        for (Map<String, Object> row : raw) {
            String key = text(row.get("adm_no"));
            Map<String, Object> out = grouped.computeIfAbsent(key, ignored -> {
                Map<String, Object> value = new LinkedHashMap<>();
                value.put("admissionNo", key); value.put("name", row.get("name")); value.put("className", row.get("class"));
                value.put("fatherName", row.get("father_name")); value.put("mobileNo", row.get("mobile_no"));
                value.put("installmentNo", row.get("installment_no")); value.put("feeHeads", new LinkedHashMap<String, Object>());
                value.put("totalFee", 0d); value.put("totalPaid", 0d); value.put("totalDue", 0d); value.put("lateFee", 0d); value.put("status", "Registered");
                return value;
            });
            @SuppressWarnings("unchecked") Map<String, Object> heads = (Map<String, Object>) out.get("feeHeads");
            String head = text(row.get("fee_head")); if (!head.isBlank()) heads.put(head, row.get("total_fee"));
            out.put("totalFee", number(out.get("totalFee")) + number(row.get("total_fee")));
            out.put("totalPaid", number(out.get("totalPaid")) + number(row.get("total_paid")));
            out.put("totalDue", number(out.get("totalDue")) + number(row.get("total_due")));
        }
        return new ArrayList<>(grouped.values());
    }

    private static double number(Object value) { try { return Double.parseDouble(text(value)); } catch (Exception ignored) { return 0d; } }

    @GetMapping("/late-fee")
    public List<Map<String, Object>> lateFee(@RequestParam(required = false) String session,
                                             @RequestParam(defaultValue = "0") int feeTypeId,
                                             Authentication authentication) {
        String sc = school(authentication), ses = session(session, sc);
        String type = feeTypeId == 6 || feeTypeId == 7 ? " AND lf.fee_type_id=?" : " AND lf.fee_type_id=0";
        List<Object> p = new ArrayList<>(List.of(sc,ses,sc,ses)); if (feeTypeId == 6 || feeTypeId == 7) p.add(feeTypeId);
        return jdbc.queryForList("SELECT sm.registration_no AS adm_no,UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'')))) AS name,COALESCE(cn.class_name,'')+' ('+COALESCE(tc.section,'')+')' AS class,COALESCE(so.father_name,'') AS father_name,s.inst_no,COALESCE(p.pay_mode,p.mop,'') AS pay_mode,CONVERT(varchar(20),TRY_CONVERT(date,COALESCE(p.pay_date,CONVERT(varchar(30),s.pay_date,121))),103) AS pay_date,COALESCE(TRY_CONVERT(float,s.late_fee),0) AS late_fee FROM stu_fee_deposit s JOIN TB_StudentMaster sm ON sm.school_id=s.school_id AND sm.stu_id=s.stu_id JOIN student_other_info so ON so.school_id=sm.school_id AND so.registration_no=sm.registration_no AND so.uid=sm.uid LEFT JOIN class_name cn ON cn.school_id=sm.school_id AND cn.id=sm.class_id LEFT JOIN TB_Class tc ON tc.school_id=sm.school_id AND tc.sec_id=sm.sec_id LEFT JOIN fee_payment_detail p ON p.school_id=s.school_id AND (p.receiptno=s.receipt_no OR p.receipt_no=s.receipt_no) AND p.status='Active' WHERE s.school_id=? AND s.session=? AND LOWER(s.status)='active' AND COALESCE(TRY_CONVERT(float,s.late_fee),0)>0 AND EXISTS (SELECT 1 FROM late_fee lf WHERE lf.school_id=? AND lf.session=? AND lf.inst_no=s.inst_no" + type + ") ORDER BY s.id", p.toArray());
    }
}
