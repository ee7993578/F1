package com.edufox.schoolerp.studentportal.portal.student_portal;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;

/**
 * Direct SQL implementation of the legacy student portal reads.
 *
 * The ASPX portal used several stored procedures.  This repository deliberately
 * keeps the same tables and filters, but expresses the reads as parameterised
 * SQL so the migrated service is portable to the future MySQL schema.
 */
@Repository
@RequiredArgsConstructor
public class StudentPortalRepository {
    private final JdbcTemplate jdbc;

    public record StudentContext(String studentId, String registrationNo, String session, int classId, int sectionId) {}

    private static String s(Object value) { return value == null ? "" : String.valueOf(value).trim(); }
    private static int i(Object value) { try { return Integer.parseInt(s(value)); } catch (Exception ignored) { return 0; } }
    private static double d(Object value) { try { return Double.parseDouble(s(value)); } catch (Exception ignored) { return 0D; } }
    private static Map<String, Object> row(Object... values) {
        Map<String, Object> out = new LinkedHashMap<>();
        for (int n = 0; n + 1 < values.length; n += 2) out.put(String.valueOf(values[n]), values[n + 1]);
        return out;
    }
    private static Object value(Map<String, Object> row, String key) {
        if (row.containsKey(key)) return row.get(key);
        for (Map.Entry<String, Object> entry : row.entrySet()) if (entry.getKey().equalsIgnoreCase(key)) return entry.getValue();
        return null;
    }
    private static String text(Map<String, Object> row, String key) { return s(value(row, key)); }
    private static Map<String, Object> first(List<Map<String, Object>> rows) { return rows.isEmpty() ? null : rows.get(0); }

    public String currentSession(String school, String requested) {
        if (!s(requested).isBlank() && !"0".equals(s(requested))) return s(requested);
        List<Map<String, Object>> rows = jdbc.queryForList("SELECT TOP 1 session FROM TB_Session WHERE school_id=? ORDER BY CASE WHEN status='Default' THEN 0 ELSE 1 END, id DESC", school);
        return rows.isEmpty() ? "" : text(rows.get(0), "session");
    }

    /** Resolve the requested student and enforce the same student/parent relationship as the old login. */
    public StudentContext resolve(String school, String session, String staffUid, String subject, String role, String requestedStudent) {
        String selected = s(requestedStudent);
        if (selected.isBlank() || "0".equals(selected)) selected = s(staffUid);
        String relation = "parent".equalsIgnoreCase(role) ? "(so.Mobile=? OR so.Parent_id=?)" : "(so.Student_id=? OR CAST(st.stu_id AS varchar(50))=?)";
        List<Object> params = new ArrayList<>(List.of(school, session, selected));
        if ("parent".equalsIgnoreCase(role)) {
            params.add(subject);
            params.add(subject);
        } else {
            params.add(subject);
            params.add(staffUid);
        }
        String sql = """
            SELECT TOP 1 st.stu_id AS studentId,st.registration_no AS registrationNo,st.class_id AS classId,st.sec_id AS sectionId
            FROM TB_StudentMaster st
            JOIN student_other_info so ON so.school_id=st.school_id AND so.registration_no=st.registration_no
            WHERE st.school_id=? AND st.session=? AND CAST(st.stu_id AS varchar(50))=?
              AND (""" + relation + ")";
        try {
            Map<String, Object> r = first(jdbc.queryForList(sql, params.toArray()));
            if (r == null) throw forbidden("The selected student is not linked to this login");
            return new StudentContext(text(r, "studentId"), text(r, "registrationNo"), session, i(value(r, "classId")), i(value(r, "sectionId")));
        } catch (EmptyResultDataAccessException e) {
            throw forbidden("The selected student is not linked to this login");
        }
    }

    public List<Map<String, Object>> studentsForLogin(String school, String session, String staffUid, String subject, String role) {
        String relation = "parent".equalsIgnoreCase(role) ? "(so.Mobile=? OR so.Parent_id=?)" : "(so.Student_id=? OR CAST(st.stu_id AS varchar(50))=?)";
        List<Object> params = new ArrayList<>(List.of(school, session));
        if ("parent".equalsIgnoreCase(role)) { params.add(subject); params.add(subject); } else { params.add(subject); params.add(staffUid); }
        return jdbc.queryForList("""
            SELECT DISTINCT st.stu_id AS studentId,st.registration_no AS registrationNo,
              UPPER(LTRIM(RTRIM(CONCAT(COALESCE(so.first_name,''),' ',COALESCE(so.mid_name,''),' ',COALESCE(so.last_name,''))))) AS studentName,
              cn.class_name AS className,cl.section,cn.class_order AS classOrder
            FROM TB_StudentMaster st
            JOIN student_other_info so ON so.school_id=st.school_id AND so.registration_no=st.registration_no
            LEFT JOIN TB_Class cl ON cl.school_id=st.school_id AND cl.sec_id=st.sec_id
            LEFT JOIN class_name cn ON cn.school_id=st.school_id AND cn.id=st.class_id
            WHERE st.school_id=? AND st.session=? AND (""" + relation + ") ORDER BY cn.class_order,st.registration_no", params.toArray());
    }

    public Map<String, Object> profile(String school, StudentContext c) {
        Map<String, Object> result = first(jdbc.queryForList("""
            SELECT TOP 1 st.stu_id AS studentId,st.registration_no AS registrationNo,st.rollno AS rollNo,
              st.admission_date AS admissionDate,st.class_id AS classId,st.sec_id AS sectionId,
              UPPER(LTRIM(RTRIM(CONCAT(COALESCE(so.first_name,''),' ',COALESCE(so.mid_name,''),' ',COALESCE(so.last_name,''))))) AS name,
              COALESCE(so.gender,'') AS gender,COALESCE(so.dob,'') AS dob,COALESCE(so.father_name,'') AS fatherName,
              COALESCE(so.mother_name,'') AS motherName,COALESCE(so.phone,'') AS phone,COALESCE(so.mobile,'') AS mobile,
              COALESCE(so.r_address,'') AS address,COALESCE(so.studentimage,'') AS rawImage,
              COALESCE(cn.class_name,'') AS className,COALESCE(cl.section,'') AS section,
              COALESCE((SELECT TOP 1 CONCAT(COALESCE(f.first_name,''),' ',COALESCE(f.mid_name,''),' ',COALESCE(f.last_name,''))
                FROM TB_FacultyMaster f WHERE f.school_id=st.school_id AND f.f_id=(SELECT TOP 1 ct.f_id FROM TB_ClassTeacher ct WHERE ct.school_id=st.school_id AND ct.session=st.session AND ct.section_id=st.sec_id)), '') AS classTeacher,
              COALESCE(sm.web_address,'') AS webAddress
            FROM TB_StudentMaster st
            LEFT JOIN student_other_info so ON so.school_id=st.school_id AND so.registration_no=st.registration_no
            LEFT JOIN TB_Class cl ON cl.school_id=st.school_id AND cl.sec_id=st.sec_id
            LEFT JOIN class_name cn ON cn.school_id=st.school_id AND cn.id=st.class_id
            LEFT JOIN schoolmaster sm ON sm.school_id=st.school_id
            WHERE st.school_id=? AND st.session=? AND st.stu_id=?""", school, c.session(), c.studentId()));
        if (result == null) return row("studentId", c.studentId(), "registrationNo", c.registrationNo());
        String image = text(result, "rawImage").replace("~/", "/");
        String web = text(result, "webAddress");
        result.put("imagePath", image.isBlank() ? "" : (image.toLowerCase(Locale.ROOT).startsWith("http") ? image : web + image));
        result.remove("rawImage");
        result.remove("webAddress");
        result.put("session", c.session());
        result.put("classSection", (text(result, "className") + (text(result, "section").isBlank() ? "" : " (" + text(result, "section") + ")")).trim());
        return result;
    }

    public List<Map<String, Object>> syllabus(String school, StudentContext c) {
        return jdbc.queryForList("""
            SELECT syllabus_id AS id,title,REPLACE(file_path,'~/','') AS filePath,COALESCE(subject_name,'') AS subjectName
            FROM upload_syllabus WHERE school_id=? AND session=? AND status=1 AND TRY_CONVERT(int,class_id)=?
            ORDER BY created_date DESC,id DESC""", school, c.session(), c.classId());
    }

    public List<Map<String, Object>> circulars(String school, String session) {
        return jdbc.queryForList("""
            SELECT cir_id AS id,cir_date AS circularDate,cir_title AS title,REPLACE(cir_files,'~/','') AS filePath,
              cir_month AS month FROM circulars WHERE school_id=? AND session=? AND status=1
              AND (TRY_CONVERT(int,cir_month)=DATEPART(month,GETDATE()) OR cir_month IS NULL OR cir_month='')
            ORDER BY TRY_CONVERT(date,cir_date) DESC,id DESC""", school, session);
    }

    public List<Map<String, Object>> holidayHomework(String school, StudentContext c) {
        return jdbc.queryForList("""
            SELECT title_id AS id,title,REPLACE(file_path,'~/','') AS filePath,submission_date AS submissionDate
            FROM holiday_homework h WHERE h.school_id=? AND h.session=? AND h.status=1
              AND (TRY_CONVERT(int,h.sec_id)=? OR (NOT EXISTS(SELECT 1 FROM holiday_homework x WHERE x.school_id=h.school_id AND x.session=h.session AND x.status=1 AND TRY_CONVERT(int,x.sec_id)=?) AND TRY_CONVERT(int,h.class_id)=?))
            ORDER BY h.created_date DESC,h.id DESC""", school, c.session(), c.sectionId(), c.sectionId(), c.classId());
    }

    public List<Map<String, Object>> dailyHomework(String school, StudentContext c, String date) {
        String selected = s(date).isBlank() ? LocalDate.now().toString() : s(date);
        return jdbc.queryForList("""
            SELECT d.homework_date AS homeworkDate,COALESCE(sub.subject,'') AS subject,d.homework_desc AS homework,
              REPLACE(COALESCE(d.file_path,''),'~/','') AS filePath
            FROM daily_homework d LEFT JOIN TB_SubjectMaster sub ON sub.school_id=d.school_id AND sub.id=TRY_CONVERT(int,d.subject_id)
            WHERE d.school_id=? AND d.session=? AND TRY_CONVERT(int,d.sec_id)=?
              AND (d.homework_date=? OR TRY_CONVERT(date,d.homework_date)=TRY_CONVERT(date,?))
            UNION ALL
            SELECT d.homework_date,COALESCE(sub.subject,''),d.homework_desc,REPLACE(COALESCE(d.file_path,''),'~/','')
            FROM daily_homework d LEFT JOIN TB_SubjectMaster sub ON sub.school_id=d.school_id AND sub.id=TRY_CONVERT(int,d.subject_id)
            WHERE d.school_id=? AND d.session=? AND d.sec_id IN (SELECT vclass_id FROM tb_virtualclass WHERE school_id=? AND stu_id=? AND session=?)
              AND (d.homework_date=? OR TRY_CONVERT(date,d.homework_date)=TRY_CONVERT(date,?))
            ORDER BY homeworkDate DESC""", school, c.session(), c.sectionId(), selected, selected, school, c.session(), school, c.studentId(), c.session(), selected, selected);
    }

    public List<Map<String, Object>> assignments(String school, StudentContext c) {
        return jdbc.queryForList("""
            SELECT Assignment_id AS id,Message AS message,PostedDate AS postedDate,SubmissionDate AS submissionDate,
              REPLACE(COALESCE(FilePath,''),'~/','') AS filePath,COALESCE(Read_Status,'') AS readStatus
            FROM TB_AssignmentMaster WHERE school_id=? AND session=? AND status=1 AND stu_id=?
            ORDER BY PostedDate DESC,Assignment_id DESC""", school, c.session(), c.studentId());
    }

    public List<Map<String, Object>> feeRows(String school, StudentContext c) {
        List<Map<String, Object>> rows = jdbc.queryForList("""
            SELECT l.installment_no AS installmentNo,
              SUM(COALESCE(l.head_amount,0)) AS installmentAmount,SUM(COALESCE(l.concession,0)) AS concession,
              SUM(COALESCE(l.payable,0)) AS payable,SUM(COALESCE(l.receive_amount,0)) AS paid,SUM(COALESCE(l.balance,0)) AS balance
            FROM fee_student_ledger l WHERE l.school_id=? AND l.session=? AND l.stu_id=?
            GROUP BY l.installment_no,l.school_id,l.session,l.stu_id ORDER BY l.installment_no""", school, c.session(), c.studentId());
        Map<String, Map<String, Object>> metadata = new HashMap<>();
        for (Map<String, Object> meta : jdbc.queryForList("""
            SELECT i.installment_no AS installmentNo,CONVERT(varchar(50),MIN(i.due_date),106) AS dueDate,
              m.month_name AS monthName,CONVERT(varchar(10),MIN(i.due_date),23) AS dueDateIso FROM Installment_Master i LEFT JOIN tb_month m ON m.month_id=i.month_id
            WHERE i.school_id=? AND i.session=? GROUP BY i.installment_no,m.month_name,i.month_id ORDER BY i.installment_no,i.month_id""", school, c.session())) {
            String key = text(meta, "installmentNo");
            Map<String, Object> item = metadata.computeIfAbsent(key, ignored -> row("dueDate", text(meta, "dueDate"), "dueDateIso", text(meta, "dueDateIso"), "months", new ArrayList<String>()));
            @SuppressWarnings("unchecked") List<String> months = (List<String>) item.get("months");
            String month = text(meta, "monthName");
            if (!month.isBlank() && !months.contains(month)) months.add(month);
        }
        Map<String, Map<String, Object>> lateFees = new HashMap<>();
        for (Map<String, Object> late : jdbc.queryForList("SELECT installment_no AS installmentNo,SUM(COALESCE(late_fee,0)) AS lateFee FROM stu_latefee_ledger WHERE school_id=? AND session=? AND stu_id=? GROUP BY installment_no", school, c.session(), c.studentId())) lateFees.put(text(late, "installmentNo"), late);
        for (Map<String, Object> r : rows) {
            Map<String, Object> meta = metadata.get(text(r, "installmentNo"));
            List<String> months = meta == null ? List.of() : (List<String>) meta.getOrDefault("months", List.of());
            r.put("months", String.join(",", months.stream().map(x -> x.substring(0, Math.min(3, x.length())).toUpperCase(Locale.ROOT)).toList()));
            r.put("dueDate", meta == null ? "" : text(meta, "dueDate"));
            r.put("dueDateIso", meta == null ? "" : text(meta, "dueDateIso"));
            r.put("lateFee", lateFees.containsKey(text(r, "installmentNo")) ? d(value(lateFees.get(text(r, "installmentNo")), "lateFee")) : 0D);
        }
        boolean online = !jdbc.queryForList("SELECT 1 FROM schoolmaster WHERE school_id=? AND ISNULL(OnlineFeePayment_Status,0)=1", school).isEmpty();
        boolean payAssigned = false;
        double totalPayable = 0, totalPaid = 0, totalBalance = 0;
        for (Map<String, Object> r : rows) {
            double balance = d(value(r, "balance"));
            String status = balance <= 0.0001 ? "Paid" : (online && !payAssigned ? "Pay now" : "Due");
            if (balance > 0.0001 && !payAssigned) payAssigned = true;
            r.put("status", status);
            r.put("balanceWithLateFee", balance + d(value(r, "lateFee")));
            totalPayable += d(value(r, "payable")); totalPaid += d(value(r, "paid")); totalBalance += balance;
        }
        if (!rows.isEmpty()) {
            Map<String, Object> firstDue = rows.stream().filter(r -> d(value(r, "balance")) > 0.0001).findFirst().orElse(rows.get(0));
            for (Map<String, Object> r : rows) {
                r.put("totalPayable", totalPayable); r.put("totalPaid", totalPaid); r.put("totalBalance", totalBalance);
                r.put("nextDue", firstDue == r ? d(value(r, "payable")) : 0D);
                r.put("nextDueDate", firstDue == r ? text(firstDue, "dueDate") : "");
            }
        }
        return rows;
    }

    public Map<String, Object> paymentUrl(String school, StudentContext c, String installmentNo) {
        Map<String, Object> fee = feeRows(school, c).stream().filter(r -> text(r, "installmentNo").equals(s(installmentNo))).findFirst().orElse(null);
        if (fee == null || !"Pay now".equalsIgnoreCase(text(fee, "status"))) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No installment available for payment.");
        String dueDate = text(fee, "dueDateIso");
        String plain = "Amount=" + String.format(Locale.US, "%.2f", d(value(fee, "balanceWithLateFee")))
            + "&stuid=" + c.studentId() + "&instalmentno=" + text(fee, "installmentNo")
            + "&due_date=" + dueDate + "&latefee=" + String.format(Locale.US, "%.2f", d(value(fee, "lateFee")))
            + "&page=student&usertype=student&staff_u_id=&session=" + c.session();
        try {
            String encoded = URLEncoder.encode(encryptLegacy(plain), StandardCharsets.UTF_8);
            List<Map<String, Object>> schools = jdbc.queryForList("SELECT TOP 1 web_address AS webAddress FROM schoolmaster WHERE school_id=?", school);
            String base = schools.isEmpty() ? "" : text(schools.get(0), "webAddress");
            if (!base.isBlank() && !base.endsWith("/")) base += "/";
            return row("url", base + "PaymentGateway.aspx?q=" + encoded, "installmentNo", text(fee, "installmentNo"), "amount", value(fee, "balanceWithLateFee"));
        } catch (GeneralSecurityException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not prepare payment request", e);
        }
    }

    private static String encryptLegacy(String plain) throws GeneralSecurityException {
        byte[] salt = new byte[] { 0x01, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF };
        PBEKeySpec spec = new PBEKeySpec("EduFoxSecret123".toCharArray(), salt, 1000, 384);
        byte[] derived = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(spec).getEncoded();
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(Arrays.copyOfRange(derived, 0, 32), "AES"), new IvParameterSpec(Arrays.copyOfRange(derived, 32, 48)));
        return Base64.getEncoder().encodeToString(cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8)));
    }

    public List<Map<String, Object>> transactions(String school, StudentContext c) {
        List<Map<String, Object>> rows = jdbc.queryForList("""
            SELECT receiptno AS receiptNo,paymentdate AS paymentDate,mop,
              transaction_no AS transactionNo,transaction_date AS transactionDate,amount,uid,
              COALESCE(status,'') AS status FROM fee_payment_detail p
            WHERE p.school_id=? AND p.financial_year=? AND p.admno=? AND ISNULL(p.Inactive_Remark,'')<>'Chargeback'
            ORDER BY TRY_CONVERT(date,p.paymentdate),p.row_id""", school, c.session(), c.registrationNo());
        Set<String> uids = rows.stream().map(r -> text(r, "uid")).filter(x -> !x.isBlank()).collect(java.util.stream.Collectors.toSet());
        Map<String, List<String>> installments = new HashMap<>(); Map<String, Double> lateFees = new HashMap<>();
        if (!uids.isEmpty()) {
            String marks = String.join(",", Collections.nCopies(uids.size(), "?"));
            List<Object> params = new ArrayList<>(); params.add(school); params.addAll(uids);
            for (Map<String, Object> dep : jdbc.queryForList("SELECT uid,installment_no AS installmentNo,SUM(COALESCE(inst_late_fee,0)) AS lateFee FROM fee_installmentwise_deposit WHERE school_id=? AND uid IN (" + marks + ") GROUP BY uid,installment_no", params.toArray())) {
                String uid = text(dep, "uid"); installments.computeIfAbsent(uid, ignored -> new ArrayList<>()).add(text(dep, "installmentNo")); lateFees.merge(uid, d(value(dep, "lateFee")), Double::sum);
            }
        }
        for (Map<String, Object> r : rows) { String uid = text(r, "uid"); r.put("installmentNo", String.join(",", installments.getOrDefault(uid, List.of()))); r.put("lateFee", lateFees.getOrDefault(uid, 0D)); r.remove("uid"); }
        return rows;
    }

    public List<Map<String, Object>> attendance(String school, StudentContext c) {
        String type = "1";
        List<Map<String, Object>> config = jdbc.queryForList("SELECT TOP 1 attendance_type FROM configuration WHERE school_id=?", school);
        if (!config.isEmpty() && !text(config.get(0), "attendance_type").isBlank()) type = text(config.get(0), "attendance_type");
        if ("2".equals(type)) return jdbc.queryForList("""
            SELECT m.month_id AS monthId,LEFT(m.month_name,3) AS monthName,COALESCE(a.present,0) AS present,COALESCE(a.absent,0) AS absent,
              COALESCE(a.present,0)+COALESCE(a.absent,0) AS total,
              CONCAT(CAST(COALESCE(a.present,0) AS varchar(20)),'/',CAST(COALESCE(a.present,0)+COALESCE(a.absent,0) AS varchar(20))) AS att,
              CONCAT(CAST(CASE WHEN COALESCE(a.present,0)+COALESCE(a.absent,0)=0 THEN 0 ELSE (COALESCE(a.present,0)*100)/(COALESCE(a.present,0)+COALESCE(a.absent,0)) END AS varchar(20)),' %') AS percentage
            FROM tb_month m LEFT JOIN (SELECT MONTH(TRY_CONVERT(date,att_date)) monthId,SUM(CASE WHEN att_status='P' THEN 1 ELSE 0 END) present,SUM(CASE WHEN att_status='A' THEN 1 ELSE 0 END) absent FROM day_attendance WHERE school_id=? AND session=? AND stu_id=? GROUP BY MONTH(TRY_CONVERT(date,att_date))) a ON a.monthId=m.month_id ORDER BY m.id""", school, c.session(), c.studentId());
        return jdbc.queryForList("""
            SELECT m.month_id AS monthId,LEFT(m.month_name,3) AS monthName,COALESCE(ma.attendance,0) AS present,COALESCE(ma.work_day,0) AS total,
              COALESCE(ma.work_day,0)-COALESCE(ma.attendance,0) AS absent,
              CONCAT(CAST(COALESCE(ma.attendance,0) AS varchar(20)),'/',CAST(COALESCE(ma.work_day,0) AS varchar(20))) AS att,
              CONCAT(CAST(CASE WHEN COALESCE(ma.work_day,0)=0 THEN 0 ELSE (COALESCE(ma.attendance,0)*100)/COALESCE(ma.work_day,0) END AS varchar(20)),' %') AS percentage
            FROM tb_month m LEFT JOIN month_attendance ma ON ma.school_id=? AND ma.session=? AND ma.stu_id=? AND ma.month_id=m.month_id ORDER BY m.id""", school, c.session(), c.studentId());
    }

    public List<Map<String, Object>> attendanceDetail(String school, StudentContext c, String monthId) {
        return jdbc.queryForList("""
            SELECT CONVERT(varchar(50),TRY_CONVERT(date,att_date),106) AS attendanceDate,
              MAX(CASE WHEN att_status='P' THEN 'Present' ELSE '-' END) AS present,
              MAX(CASE WHEN att_status='A' THEN 'Absent' ELSE '-' END) AS absent
            FROM day_attendance WHERE school_id=? AND session=? AND stu_id=? AND MONTH(TRY_CONVERT(date,att_date))=?
            GROUP BY TRY_CONVERT(date,att_date) ORDER BY TRY_CONVERT(date,att_date)""", school, c.session(), c.studentId(), i(monthId));
    }

    public List<Map<String, Object>> timetable(String school, StudentContext c) {
        return jdbc.queryForList("""
            SELECT tt.TDay AS day,tt.Period AS period,COALESCE(sub.Subject,'') AS subject,tt.TT_ID AS timetableId,tt.TT_UID AS timetableUid
            FROM TB_TimeTable tt LEFT JOIN TB_SubjectMaster sub ON sub.school_id=tt.school_id AND sub.id=tt.Subject_id
            WHERE tt.school_id=? AND tt.session=? AND tt.sec_id=? ORDER BY tt.TDay,TRY_CONVERT(int,tt.Period),tt.id""", school, c.session(), c.sectionId());
    }

    public List<Map<String, Object>> examSchedule(String school, StudentContext c) {
        return jdbc.queryForList("""
            SELECT es.term_id AS termId,COALESCE(t.term_name,'') AS termName,es.exam_type_id AS examTypeId,
              COALESCE(es.exam_name_dis,et.exam_type,'') AS examType,em.e_date AS examDate,em.subject_id AS subjectId,
              COALESCE(sub.Subject,'') AS subjectName,em.max_marks AS maxMarks
            FROM exam_set es JOIN assign_exam_set ae ON ae.school_id=es.school_id AND ae.exam_set_id=es.exam_set_id AND ae.class_id=?
              LEFT JOIN term t ON t.term_id=es.term_id LEFT JOIN exam_type et ON et.school_id=es.school_id AND et.exam_type_id=es.exam_type_id
              LEFT JOIN tb_exam_max_min_new em ON em.school_id=es.school_id AND em.session=? AND em.class_id=? AND em.exam_type_id=es.exam_type_id AND em.term_id=es.term_id
              AND ISNULL(em.Grade_subject,'n')='n' AND ISNULL(em.max_marks,0)<>0
              LEFT JOIN TB_SubjectMaster sub ON sub.school_id=em.school_id AND sub.id=em.subject_id
            WHERE es.school_id=? AND ae.from_year<=TRY_CONVERT(int,LEFT(?,4)) AND (ae.to_year=0 OR ae.to_year>=TRY_CONVERT(int,RIGHT(?,4)))
            ORDER BY es.term_id,es.exam_order,em.e_date,em.id""", c.classId(), c.session(), c.classId(), school, c.session(), c.session());
    }

    public List<Map<String, Object>> academicPerformance(String school, StudentContext c, String termId) {
        List<Map<String, Object>> senior = jdbc.queryForList("""
            SELECT sm.Exam_type_id AS examTypeId,COALESCE(es.exam_name_dis,et.exam_type,'') AS examType,
              sm.Term_id AS termId,sm.Subject_id AS subjectId,COALESCE(sub.Subject,'') AS subjectName,
              TRY_CONVERT(decimal(18,2),NULLIF(sm.marks,'')) AS marks,TRY_CONVERT(decimal(18,2),NULLIF(sm.max_marks,'')) AS maxMarks,
              sm.Weightage_Marks AS weightageMarks
            FROM senior_marks sm LEFT JOIN exam_type et ON et.school_id=sm.school_id AND et.exam_type_id=TRY_CONVERT(int,sm.Exam_type_id)
              LEFT JOIN exam_set es ON es.school_id=sm.school_id AND es.exam_type_id=TRY_CONVERT(int,sm.Exam_type_id) AND es.term_id=TRY_CONVERT(int,sm.Term_id)
              LEFT JOIN TB_SubjectMaster sub ON sub.school_id=sm.school_id AND sub.id=TRY_CONVERT(int,sm.Subject_id)
            WHERE sm.school_id=? AND sm.Session=? AND sm.Stu_id=?
              AND (?='' OR sm.Term_id=?) ORDER BY TRY_CONVERT(int,sm.Term_id),sm.Exam_type_id,sm.Subject_id""", school, c.session(), c.studentId(), s(termId), s(termId));
        if (!senior.isEmpty()) return senior;
        return juniorPerformance(school, c, termId);
    }

    private List<Map<String, Object>> juniorPerformance(String school, StudentContext c, String termId) {
        List<Map<String, Object>> reports = jdbc.queryForList("SELECT * FROM exam_reports WHERE school_id=? AND session=? AND STU_ID=?", school, c.session(), c.studentId());
        List<Map<String, Object>> out = new ArrayList<>();
        String[][] exams = { {"PA1", "PA1_MAX"}, {"PT1", "PT1_MAX"}, {"NB1", "NB1_MAX"}, {"SE1", "SE1_MAX"}, {"HY_TH", "HY_TH_MAX"}, {"HY_PR", "HY_PR_MAX"}, {"PA3", "PA3_MAX"}, {"PT2", "PT2_MAX"}, {"NB2", "NB2_MAX"}, {"SE2", "SE2_MAX"}, {"Y_TH", "Y_TH_MAX"}, {"Y_PR", "Y_PR_MAX"} };
        for (Map<String, Object> source : reports) for (String[] exam : exams) {
            String marks = text(source, exam[0]);
            if (marks.isBlank()) continue;
            out.add(row("examTypeId", exam[0], "examType", exam[0], "termId", termId, "subjectId", text(source, "SUB_ID"), "subjectName", text(source, "sub_NAME"), "marks", d(marks), "maxMarks", d(value(source, exam[1]))));
        }
        return out;
    }

    public List<Map<String, Object>> events(String school, StudentContext c) {
        return jdbc.queryForList("""
            SELECT TOP 20 event_id AS id,event_name AS name,event_date AS eventDate,descriptions AS description,
              MAX(file_name) AS filePath FROM latest_events WHERE school_id=? AND session=? GROUP BY event_id,event_name,event_date,descriptions
            ORDER BY TRY_CONVERT(date,event_date) DESC,TRY_CONVERT(int,event_id) DESC""", school, c.session());
    }

    public static ResponseStatusException forbidden(String message) { return new ResponseStatusException(HttpStatus.FORBIDDEN, message); }
}
