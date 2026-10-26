package com.edufox.schoolerp.fee.report.fee_reports;

import com.edufox.schoolerp.common.persistence.SessionResolver;
import com.edufox.schoolerp.fee.report.fee_booklet.*;
import com.edufox.schoolerp.fee.shared.fee_core.*;
import com.edufox.schoolerp.fee.update.fee_update.*;
import com.edufox.schoolerp.fee.update.optional_fee.*;
import com.edufox.schoolerp.fee.work.fee_work.*;

import com.edufox.schoolerp.config.DatabaseWriteGuard;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Direct SQL replacements for the two legacy ASPX reports.  These queries
 * deliberately keep the legacy table/column names and do not call a stored
 * procedure.
 */
@RestController
@RequestMapping("/api/fees/legacy-reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class LegacyFeeReportController {
    private final JdbcTemplate jdbc;
    private final DatabaseWriteGuard writeGuard;

    private static Claims claims(Authentication a) { return (Claims) a.getDetails(); }
    private static String school(Authentication a) { return text(claims(a).get("schoolId", String.class)); }
    private static String staff(Authentication a) { return text(claims(a).get("staffUid", String.class)); }
    private static String text(Object v) { return v == null ? "" : String.valueOf(v).trim(); }
    private static int integer(Object v) { try { return Integer.parseInt(text(v)); } catch (Exception e) { return 0; } }

    private String session(Object requested, String sc) {
        return SessionResolver.resolve(jdbc, requested, sc);
    }

    @GetMapping("/gateway")
    public List<Map<String, Object>> gateway(@RequestParam(defaultValue = "0") String platform,
                                             @RequestParam(required = false) String from,
                                             @RequestParam(required = false) String to,
                                             @RequestParam(defaultValue = "") String admissionNo,
                                             Authentication a) {
        String sc = school(a);
        StringBuilder q = new StringBuilder("""
            WITH aa AS (
              SELECT pr.id,pr.posteddate,
                FORMAT(pr.posteddate,'dd-MM-yyyy hh:mm:ss tt') AS paymentDate,
                pr.order_id AS orderId,pr.tracking_id AS trackingId,pr.bank_ref_no AS bankReferenceNo,
                pr.InstallmentNo AS installments,pr.order_status AS orderStatus,pr.status_message AS statusMessage,pr.E_platform AS platform,
                pr.amount,so.Registration_no AS registrationNo,tsm.FeeBookNo AS feeBook,
                LTRIM(RTRIM(ISNULL(so.first_name,'')))+
                  CASE WHEN ISNULL(so.mid_name,'')<>'' THEN ' '+so.mid_name ELSE '' END+
                  CASE WHEN ISNULL(so.last_name,'')<>'' THEN ' '+so.last_name ELSE '' END AS studentName,
                so.Father_name AS fatherName,so.Mobile AS mobileNumber,
                cn.class_name+' ('+ISNULL(tc.Section,'')+')' AS className,pr.username AS paymentUserName,
                CONVERT(varchar(100),CAST(pr.Settlement_Date AS date),106) AS settlementDate,
                CASE WHEN pr.order_status='Success' THEN 0 ELSE 1 END AS sts
              FROM PaymentResponse pr
              LEFT JOIN TB_StudentMaster tsm ON pr.school_id=tsm.school_id AND pr.stu_id=tsm.stu_id
              LEFT JOIN student_other_info so ON tsm.school_id=so.school_id AND tsm.registration_no=so.registration_no
              LEFT JOIN class_name cn ON tsm.Class_id=cn.id AND tsm.school_id=cn.school_id
              LEFT JOIN TB_Class tc ON tc.school_id=tsm.school_id AND tc.Class_id=tsm.Class_id AND tc.Sec_id=tsm.Sec_id
              WHERE pr.school_id=? AND pr.E_platform IN ('Erp','App')
                AND (?='' OR ?='0' OR tsm.Registration_NO=?)
              UNION ALL
              SELECT pr.id,pr.posteddate,FORMAT(pr.posteddate,'dd-MM-yyyy hh:mm:ss tt'),pr.order_id,pr.tracking_id,pr.bank_ref_no,
                pr.InstallmentNo,pr.order_status,pr.status_message,pr.E_platform,pr.amount,osr.registrationNO,'',
                LTRIM(RTRIM(ISNULL(osr.firstName,'')))+
                  CASE WHEN ISNULL(osr.midName,'')<>'' THEN ' '+osr.midName ELSE '' END+
                  CASE WHEN ISNULL(osr.lastName,'')<>'' THEN ' '+osr.lastName ELSE '' END,
                osr.fatherName,osr.regPhone,cn.class_name,pr.username,
                CONVERT(varchar(100),CAST(pr.Settlement_Date AS date),106),CASE WHEN pr.order_status='Success' THEN 0 ELSE 1 END
              FROM PaymentResponse pr
              LEFT JOIN onlineStudentRegistration osr ON pr.school_id=osr.school_id AND pr.stu_id=osr.registrationNO
              LEFT JOIN class_name cn ON osr.admissionClass=cn.id AND osr.school_id=cn.school_id
              WHERE pr.school_id=? AND pr.E_platform='Admission'
                AND (?='' OR ?='0' OR osr.registrationNO=?)
              UNION ALL
              SELECT pr.id,pr.posteddate,FORMAT(pr.posteddate,'dd-MM-yyyy hh:mm:ss tt'),pr.order_id,pr.tracking_id,pr.bank_ref_no,
                pr.InstallmentNo,pr.order_status,pr.status_message,pr.E_platform,pr.amount,fa.RegistrationNo,'',fa.FullName,
                fa.father_name,fa.MobileNumber,'',pr.username,
                CONVERT(varchar(100),CAST(pr.Settlement_Date AS date),106),CASE WHEN pr.order_status='Success' THEN 0 ELSE 1 END
              FROM PaymentResponse pr
              LEFT JOIN Faculty_Job_Application fa ON pr.school_id=fa.school_id AND pr.stu_id=fa.ApplicationID
              WHERE pr.school_id=? AND pr.E_platform='job'
                AND (?='' OR ?='0' OR fa.RegistrationNo=?)
            )
            SELECT paymentDate,settlementDate,orderId,trackingId,bankReferenceNo,installments,orderStatus,statusMessage,platform,amount,
                   registrationNo,feeBook,studentName,fatherName,mobileNumber,className,paymentUserName,sts
            FROM aa WHERE (?='0' OR aa.platform=?)
              AND (?='' OR CAST(aa.posteddate AS date)>=TRY_CONVERT(date,?))
              AND (?='' OR CAST(aa.posteddate AS date)<=TRY_CONVERT(date,?))
            ORDER BY id DESC
            """);
        List<Object> p = new ArrayList<>(List.of(sc, admissionNo, admissionNo, admissionNo,
            sc, admissionNo, admissionNo, admissionNo,
            sc, admissionNo, admissionNo, admissionNo,
            platform, platform, text(from), text(from), text(to), text(to)));
        return jdbc.queryForList(q.toString(), p.toArray());
    }

    @GetMapping("/gateway/students")
    public List<Map<String, Object>> gatewayStudents(@RequestParam String query, Authentication a) {
        String sc = school(a), v = "%" + text(query) + "%";
        return jdbc.queryForList("SELECT TOP 20 sm.registration_no AS admissionNo,UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.last_name,'')))) AS studentName,COALESCE(cn.class_name,'')+' ('+COALESCE(cl.section,'')+')' AS className,COALESCE(so.father_name,'') AS fatherName,COALESCE(so.Mobile,'') AS mobileNumber FROM TB_StudentMaster sm JOIN student_other_info so ON so.school_id=sm.school_id AND so.registration_no=sm.registration_no LEFT JOIN class_name cn ON cn.school_id=sm.school_id AND cn.id=sm.class_id LEFT JOIN TB_Class cl ON cl.school_id=sm.school_id AND cl.sec_id=sm.sec_id WHERE sm.school_id=? AND (sm.registration_no LIKE ? OR so.first_name LIKE ? OR so.last_name LIKE ?) ORDER BY so.first_name", sc, v, v, v);
    }

    @GetMapping("/defaulter/bootstrap")
    public Map<String, Object> defaulterBootstrap(@RequestParam(required = false) String session, Authentication a) {
        String sc = school(a), ses = session(session, sc);
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("session", ses);
        out.put("sessions", jdbc.queryForList("SELECT id,session AS value,session AS label,status FROM TB_Session WHERE school_id=? ORDER BY id DESC", sc));
        out.put("classes", jdbc.queryForList("SELECT b.sec_id AS value,b.sec_id AS id,a.class_name+' ('+b.section+')' AS label,a.class_name AS className,b.section FROM class_name a JOIN TB_Class b ON a.id=b.class_id AND a.school_id=b.school_id WHERE a.school_id=? ORDER BY b.class_id,b.section", sc));
        out.put("feeTypes", List.of(Map.of("value", 0, "label", "Regular"), Map.of("value", 6, "label", "Transport"), Map.of("value", 7, "label", "Hostel")));
        out.put("installments", installments(sc, ses, 0));
        Map<String, Object> flags = new LinkedHashMap<>();
        try {
            Map<String, Object> c = jdbc.queryForMap("SELECT TOP 1 COALESCE(considerTransport,0) AS transport,COALESCE(considerHostel,0) AS hostel FROM configuration WHERE school_id=?", sc);
            flags.put("transport", integer(c.get("transport")) == 1); flags.put("hostel", integer(c.get("hostel")) == 1);
        } catch (Exception ignored) { flags.put("transport", true); flags.put("hostel", false); }
        out.put("visibility", flags);
        return out;
    }

    @GetMapping("/defaulter/installments")
    public List<Map<String, Object>> defaulterInstallments(@RequestParam(required = false) String session,
                                                           @RequestParam(defaultValue = "0") int feeTypeId, Authentication a) {
        return installments(school(a), session(session, school(a)), feeTypeId);
    }

    private List<Map<String, Object>> installments(String sc, String ses, int type) {
        String q = "SELECT DISTINCT installment_no AS value,installment_no AS label FROM Installment_Master WHERE school_id=? AND session=? AND status=1";
        List<Object> p = new ArrayList<>(List.of(sc, ses));
        if (type == 6 || type == 7) { q += " AND fee_type_id=?"; p.add(type); }
        else { q += " AND fee_type_id NOT IN (6,7)"; }
        return jdbc.queryForList(q + " ORDER BY installment_no", p.toArray());
    }

    @GetMapping("/defaulter")
    public List<Map<String, Object>> defaulter(@RequestParam(required = false) String session,
                                               @RequestParam(defaultValue = "0") int installmentNo,
                                               @RequestParam(defaultValue = "0") int feeTypeId,
                                               @RequestParam(defaultValue = "0") int classId,
                                               @RequestParam(defaultValue = "2") int reportType,
                                               @RequestParam(required = false) String asOnDate,
                                               Authentication a) {
        String sc = school(a), ses = session(session, sc);
        StringBuilder q = new StringBuilder("""
            WITH B AS (
              SELECT sm.stu_id,sm.registration_no AS adm_no,
                LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,''))) AS name,
                COALESCE(so.father_name,'') AS father_name,COALESCE(CAST(so.quota AS varchar(50)),'') AS quota,COALESCE(so.r_address,'') AS address,
                COALESCE(so.phone,'') AS phone,COALESCE(so.mobile,'') AS mobile,sm.class_id,sm.sec_id,
                cn.class_name+' ('+COALESCE(tc.section,'')+')' AS className
              FROM TB_StudentMaster sm JOIN student_other_info so ON so.school_id=sm.school_id AND so.registration_no=sm.registration_no AND so.uid=sm.uid
              JOIN class_name cn ON cn.school_id=sm.school_id AND cn.id=sm.class_id
              JOIN TB_Class tc ON tc.school_id=sm.school_id AND tc.sec_id=sm.sec_id AND tc.class_id=sm.class_id
              WHERE sm.school_id=? AND sm.session=? AND LOWER(sm.status)='registered' AND (so.RTE='0' OR so.RTE IS NULL)
            ), C AS (
              SELECT b.stu_id,b.adm_no,b.name,b.father_name,b.quota,b.address,b.phone,b.mobile,b.class_id,b.sec_id,b.className,
                im.installment_no,MAX(COALESCE(im.installment_dis,'')) AS month_full_name,
                SUM(COALESCE(fc.fee_charges,0)) AS Charges,
                SUM(CASE WHEN COALESCE(con.con_type,'') IN ('Percentage','%','1') THEN COALESCE(fc.fee_charges,0)*COALESCE(con.con_per,0)/100 ELSE COALESCE(con.con_charge,0) END) AS Concession,
                CAST(0 AS decimal(18,2)) AS WaveOff
              FROM B b JOIN Installment_Master im ON im.school_id=? AND im.session=? AND im.status=1
              JOIN fee_head_master h ON h.school_id=im.school_id AND h.session=im.session AND h.fee_head_id=im.fee_head_id AND h.fee_type_id=im.fee_type_id AND h.status=1
              LEFT JOIN fee_head_charge fc ON fc.school_id=h.school_id AND fc.session=h.session AND fc.fee_head_id=h.fee_head_id AND fc.fee_type_id=h.fee_type_id AND fc.class_id=b.class_id AND fc.status=1
              LEFT JOIN (SELECT stu_id,inst_no,fee_head_id,fee_type_id,MAX(con_type) con_type,SUM(COALESCE(con_charge,0)) con_charge,MAX(COALESCE(con_per,0)) con_per FROM fee_concession WHERE school_id=? AND session=? AND status=1 GROUP BY stu_id,inst_no,fee_head_id,fee_type_id) con ON con.stu_id=b.stu_id AND con.inst_no=im.installment_no AND con.fee_head_id=h.fee_head_id AND con.fee_type_id=h.fee_type_id
              LEFT JOIN Assign_Optional_Fee ao ON ao.school_id=? AND ao.session=? AND ao.stu_id=b.stu_id AND ao.fee_head_id=h.fee_head_id AND ao.fee_type_id=h.fee_type_id AND ao.installment_no=im.installment_no AND (ao.Paid_status=0 OR ao.Paid_status IS NULL)
              WHERE (h.fee_type_id=3 OR h.isOptional=0 OR ao.id IS NOT NULL)
              GROUP BY b.stu_id,b.adm_no,b.name,b.father_name,b.quota,b.address,b.phone,b.mobile,b.class_id,b.sec_id,b.className,im.installment_no
            ), L AS (
              SELECT l.stu_id,l.installment_no,SUM(COALESCE(l.head_amount,0)) Charges,SUM(COALESCE(l.concession,0)) Concession,
                SUM(COALESCE(l.receive_amount,0)) receive_amount,SUM(COALESCE(l.balance,0)) balance
              FROM fee_student_ledger l WHERE l.school_id=? AND l.session=? GROUP BY l.stu_id,l.installment_no
            ), D AS (
              SELECT c.*,COALESCE(l.receive_amount,0) receive_amount,COALESCE(l.balance,c.Charges-c.Concession-c.WaveOff-COALESCE(l.receive_amount,0)) balance,
                COALESCE(l.Charges,c.Charges) ledgerCharges,COALESCE(l.Concession,c.Concession) ledgerConcession
              FROM C c LEFT JOIN L l ON l.stu_id=c.stu_id AND l.installment_no=c.installment_no
              WHERE (?=0 OR c.installment_no<=?)
            )
            SELECT adm_no AS admNo,MAX(name) AS name,MAX(father_name) AS fatherName,MAX(quota) AS quota,MAX(address) AS address,
              MAX(className) AS className,STRING_AGG(CAST(installment_no AS varchar(20)),', ') AS installmentNo,
              MAX(month_full_name) AS monthFullName,SUM(Charges) AS Charges,SUM(Concession) AS Concession,SUM(WaveOff) AS WaveOff,
              SUM(Charges-Concession-WaveOff) AS payable,SUM(receive_amount) AS receiveAmount,CAST(0 AS decimal(18,2)) AS previousBalance,
              SUM(balance) AS balance,MAX(phone) AS phone,MAX(mobile) AS mobile,MAX(stu_id) AS studentId
            FROM D WHERE (?=0 OR class_id IN (SELECT class_id FROM TB_Class WHERE school_id=? AND sec_id=?))
            GROUP BY adm_no
            HAVING SUM(balance)>0
            """);
        List<Object> p = new ArrayList<>(List.of(sc, ses, sc, ses, sc, ses, sc, ses, sc, ses, installmentNo, installmentNo));
        String cFilter;
        if (feeTypeId == 6 || feeTypeId == 7) { cFilter = " AND im.fee_type_id=?"; p.add(4, feeTypeId); }
        else cFilter = " AND im.fee_type_id NOT IN (6,7)";
        q.insert(q.indexOf("GROUP BY b.stu_id"), cFilter + " ");
        StringBuilder outer = new StringBuilder();
        if (reportType == 1) outer.append(" AND D.receive_amount>0");
        if (reportType == 0) outer.append(" AND D.receive_amount=0");
        if (classId != 0) { outer.append(" AND D.sec_id=?"); p.add(classId); }
        q.insert(q.indexOf("GROUP BY adm_no"), outer + " ");
        // D carries the source class/section through C, so keep the class filter
        // independent of the display label and do not expose an extra selector.
        q = new StringBuilder(q.toString().replace("WHERE (?=0 OR class_id IN (SELECT class_id FROM TB_Class WHERE school_id=? AND sec_id=?))", "WHERE 1=1"));
        return jdbc.queryForList(q.toString(), p.toArray());
    }

    @GetMapping("/defaulter/holds")
    public List<Map<String, Object>> holds(@RequestParam(defaultValue = "0") int feeTypeId, Authentication a) {
        String sc = school(a), ses = session(null, sc);
        return jdbc.queryForList("SELECT h.id,h.uptodate,h.stu_id AS studentId,sm.registration_no AS admNo,UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.last_name,'')))) AS name,COALESCE(so.father_name,'') AS fatherName,COALESCE(cn.class_name,'')+' ('+COALESCE(cl.section,'')+')' AS className,COALESCE(so.mobile,'') AS mobile FROM hold_fee_defaulters h JOIN TB_StudentMaster sm ON sm.school_id=h.school_id AND sm.stu_id=h.stu_id LEFT JOIN student_other_info so ON so.school_id=sm.school_id AND so.registration_no=sm.registration_no LEFT JOIN class_name cn ON cn.school_id=sm.school_id AND cn.id=sm.class_id LEFT JOIN TB_Class cl ON cl.school_id=sm.school_id AND cl.sec_id=sm.sec_id WHERE h.school_id=? AND h.session=? AND h.status=1 AND (?=0 OR h.fee_type_id=?) ORDER BY h.id DESC", sc, ses, feeTypeId, feeTypeId);
    }

    @PostMapping("/defaulter/holds")
    public Map<String, Object> hold(@RequestBody Map<String, Object> body, Authentication a) {
        writeGuard.requireWritable();
        String sc = school(a), ses = session(body.get("session"), sc);
        int studentId = integer(body.get("studentId")), type = integer(body.get("feeTypeId"));
        String upto = text(body.get("uptoDate"));
        if (studentId == 0 || upto.isBlank()) throw new IllegalArgumentException("Student and up-to date are required");
        jdbc.update("INSERT INTO hold_fee_defaulters(stu_id,uptodate,status,session,school_id,created_date,created_by,fee_type_id) VALUES(?,?,1,?,?,GETDATE(),?,?)", studentId, upto, ses, sc, staff(a), type);
        return Map.of("message", "Student removed from fee defaulters list", "studentId", studentId);
    }

    @PutMapping("/defaulter/holds/{id}")
    public Map<String, Object> unhold(@PathVariable int id, Authentication a) {
        writeGuard.requireWritable();
        int changed = jdbc.update("UPDATE hold_fee_defaulters SET status=0,modified_by=?,modified_date=GETDATE() WHERE id=? AND school_id=? AND session=?", staff(a), id, school(a), session(null, school(a)));
        return Map.of("message", changed == 1 ? "Student unheld" : "Hold record not found", "updated", changed);
    }
}
