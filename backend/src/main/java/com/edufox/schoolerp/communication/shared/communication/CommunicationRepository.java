package com.edufox.schoolerp.communication.shared.communication;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;

/**
 * Direct SQL access for the legacy Communication pages.
 *
 * The ASPX pages use stored procedures in a number of places.  The migration
 * keeps their tables and columns, but expresses the reads and writes here so
 * the React application is not coupled to those procedures.
 */
@Repository
@RequiredArgsConstructor
public class CommunicationRepository {
    private final JdbcTemplate jdbc;

    public List<Map<String, Object>> sessions(String school) {
        return jdbc.queryForList("SELECT session AS value,session AS label,status FROM tb_session WHERE school_id=? ORDER BY CASE WHEN LOWER(status)='default' THEN 0 ELSE 1 END,session_year_start DESC", school);
    }

    public String defaultSession(String school) {
        List<Map<String, Object>> rows = jdbc.queryForList("SELECT TOP 1 session FROM tb_session WHERE school_id=? ORDER BY CASE WHEN LOWER(status)='default' THEN 0 ELSE 1 END,session_year_start DESC", school);
        return rows.isEmpty() ? "" : text(rows.get(0).get("session"));
    }

    public List<Map<String, Object>> classes(String school) {
        return jdbc.queryForList("SELECT c.sec_id AS id,c.sec_id AS secId,c.class_id AS classId,cn.class_name AS className,c.section,cn.class_name+' ('+c.section+')' AS label FROM TB_Class c JOIN class_name cn ON cn.id=c.class_id AND cn.school_id=c.school_id WHERE c.school_id=? ORDER BY c.class_id,c.section,c.sec_id", school);
    }

    public List<Map<String, Object>> faculty(String school) {
        return jdbc.queryForList("SELECT f_id AS id,f_id AS facultyId,UPPER(LTRIM(RTRIM(COALESCE(first_name,'')+' '+COALESCE(mid_name,'')+' '+COALESCE(last_name,'')))) AS name,COALESCE(mobile,'') AS mobile,COALESCE(phone,'') AS phone FROM TB_FacultyMaster WHERE school_id=? AND status=1 ORDER BY REPLACE(first_name,' ','')+' '+REPLACE(mid_name,' ','')+' '+REPLACE(last_name,' ','')", school);
    }

    public List<Map<String, Object>> templates(String school, boolean feeOnly) {
        String sql = "SELECT temp_id AS id,temp_id AS templateId,COALESCE(sms,'') AS message,COALESCE(sms,'') AS templateText,COALESCE(type,'') AS type FROM sms WHERE school_id=?";
        if (feeOnly) sql += " AND LOWER(COALESCE(type,''))='fee'";
        return jdbc.queryForList(sql + " ORDER BY temp_id", school);
    }

    public Map<String, Object> provider(String school) {
        List<Map<String, Object>> rows;
        try {
            rows = jdbc.queryForList("SELECT TOP 1 COALESCE(api_content,'') AS apiContent,COALESCE(api_mobile,'') AS apiMobile,COALESCE(api_message,'') AS apiMessage,COALESCE(sms_balance,'') AS balanceUrl FROM sms_providersetting WHERE project_id=? OR Project_ID=?", school, school);
        } catch (Exception ignored) {
            rows = List.of();
        }
        if (!rows.isEmpty()) return rows.get(0);
        try {
            rows = jdbc.queryForList("SELECT TOP 1 COALESCE(uid,'') AS uid,COALESCE(pin,'') AS pin,COALESCE(sender,'') AS sender,COALESCE(route,'') AS route FROM sms_credential WHERE school_id=?", school);
        } catch (Exception ignored) {
            rows = List.of();
        }
        return rows.isEmpty() ? Map.of() : rows.get(0);
    }

    public List<Map<String, Object>> smsStatuses(String school) {
        try { return jdbc.queryForList("SELECT DISTINCT status AS value,status AS label FROM sms_report WHERE school_id=? AND status IS NOT NULL AND LTRIM(RTRIM(status))<>'' ORDER BY status", school); }
        catch (Exception ignored) { return List.of(); }
    }

    public List<Map<String, Object>> emailStatuses(String school) {
        try { return jdbc.queryForList("SELECT DISTINCT EmailStatus AS value,EmailStatus AS label FROM EmailReport WHERE school_id=? AND EmailStatus IS NOT NULL AND LTRIM(RTRIM(EmailStatus))<>'' ORDER BY EmailStatus", school); }
        catch (Exception ignored) { return List.of(); }
    }

    public List<Map<String, Object>> emailTypes(String school) {
        try { return jdbc.queryForList("SELECT DISTINCT EntityType AS value,EntityType AS label FROM EmailReport WHERE school_id=? AND EntityType IS NOT NULL AND LTRIM(RTRIM(EntityType))<>'' ORDER BY EntityType", school); }
        catch (Exception ignored) { return List.of(); }
    }

    public List<Map<String, Object>> students(String school, String session, List<Integer> sectionIds, String studentType) {
        StringBuilder sql = new StringBuilder("SELECT sm.uid AS id,sm.uid AS uid,sm.stu_id AS studentId,sm.registration_no AS registrationNo,UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,''))))+' ['+sm.registration_no+']' AS name,UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'')))) AS studentName,COALESCE(so.father_name,'') AS fatherName,COALESCE(so.mobile,'') AS mobile,COALESCE(so.phone,'') AS phone,sm.sec_id AS secId,sm.class_id AS classId,COALESCE(cn.class_name,'')+' ('+COALESCE(tc.section,'')+')' AS className FROM TB_StudentMaster sm JOIN student_other_info so ON so.school_id=sm.school_id AND so.uid=sm.uid AND so.registration_no=sm.registration_no LEFT JOIN class_name cn ON cn.school_id=sm.school_id AND cn.id=sm.class_id LEFT JOIN TB_Class tc ON tc.school_id=sm.school_id AND tc.sec_id=sm.sec_id WHERE sm.school_id=? AND sm.session=? AND LOWER(sm.status)='registered'");
        List<Object> args = new ArrayList<>(List.of(school, session));
        if (!sectionIds.isEmpty()) { sql.append(" AND sm.sec_id IN (").append(String.join(",", Collections.nCopies(sectionIds.size(), "?"))).append(")"); args.addAll(sectionIds); }
        if ("1".equals(studentType)) sql.append(" AND sm.isnew=0");
        if ("3".equals(studentType)) sql.append(" AND sm.isnew=1");
        sql.append(" ORDER BY so.first_name,so.mid_name,so.last_name,sm.registration_no");
        return jdbc.queryForList(sql.toString(), args.toArray());
    }

    public List<Map<String, Object>> studentContacts(String school, String session, List<String> ids) {
        if (ids.isEmpty()) return List.of();
        String marks = String.join(",", Collections.nCopies(ids.size(), "?"));
        List<Object> args = new ArrayList<>(List.of(school, session)); args.addAll(ids);
        return jdbc.queryForList("SELECT sm.uid AS id,sm.registration_no AS userId,COALESCE(so.mobile,'') AS mobile,COALESCE(so.phone,'') AS phone FROM TB_StudentMaster sm JOIN student_other_info so ON so.school_id=sm.school_id AND so.uid=sm.uid AND so.registration_no=sm.registration_no WHERE sm.school_id=? AND sm.session=? AND sm.uid IN ("+marks+") AND LOWER(sm.status)='registered'", args.toArray());
    }

    public List<Map<String, Object>> facultyContacts(String school, List<String> ids) {
        if (ids.isEmpty()) return List.of();
        String marks = String.join(",", Collections.nCopies(ids.size(), "?"));
        List<Object> args = new ArrayList<>(List.of(school)); args.addAll(ids);
        return jdbc.queryForList("SELECT f_id AS id,CAST(f_id AS varchar(50)) AS userId,COALESCE(mobile,'') AS mobile,COALESCE(phone,'') AS phone FROM TB_FacultyMaster WHERE school_id=? AND status=1 AND f_id IN ("+marks+")", args.toArray());
    }

    /** Legacy sendsms.aspx student selector, backed by the fee ledger. */
    public List<Map<String, Object>> feeStudents(String school, String session, int feeType, int installment, int defaulterType, String classId) {
        StringBuilder sql = new StringBuilder("SELECT sm.uid AS id,sm.uid AS uid,sm.stu_id AS studentId,sm.registration_no AS admissionNo,sm.registration_no AS admNo,UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,''))))+' ['+sm.registration_no+']' AS nameSms,UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'')))) AS studentName,COALESCE(so.father_name,'') AS fatherName,COALESCE(so.mobile,'') AS mobile,COALESCE(so.phone,'') AS phone,COALESCE(cn.class_name,'')+' ('+COALESCE(tc.section,'')+')' AS className,SUM(COALESCE(l.balance,0)) AS balance,SUM(COALESCE(l.receive_amount,0)) AS received FROM fee_student_ledger l JOIN TB_StudentMaster sm ON sm.school_id=l.school_id AND sm.stu_id=l.stu_id AND sm.session=l.session JOIN student_other_info so ON so.school_id=sm.school_id AND so.uid=sm.uid AND so.registration_no=sm.registration_no LEFT JOIN class_name cn ON cn.school_id=sm.school_id AND cn.id=sm.class_id LEFT JOIN TB_Class tc ON tc.school_id=sm.school_id AND tc.sec_id=sm.sec_id WHERE l.school_id=? AND l.session=? AND LOWER(sm.status)='registered' AND COALESCE(l.balance,0)>0");
        List<Object> args = new ArrayList<>(List.of(school, session));
        if (feeType == 6 || feeType == 7) { sql.append(" AND l.fee_type_id=?"); args.add(feeType); }
        else sql.append(" AND l.fee_type_id NOT IN (6,7)");
        if (installment > 0) { sql.append(" AND l.installment_no=?"); args.add(installment); }
        if (classId != null && !classId.isBlank() && !"0".equals(classId)) { sql.append(" AND sm.sec_id=?"); args.add(classId); }
        sql.append(" GROUP BY sm.uid,sm.stu_id,sm.registration_no,so.first_name,so.mid_name,so.last_name,so.father_name,so.mobile,so.phone,cn.class_name,tc.section");
        if (defaulterType == 0) sql.append(" HAVING SUM(COALESCE(l.receive_amount,0))=0");
        if (defaulterType == 1) sql.append(" HAVING SUM(COALESCE(l.receive_amount,0))>0");
        sql.append(" ORDER BY so.first_name,so.mid_name,so.last_name,sm.registration_no");
        return jdbc.queryForList(sql.toString(), args.toArray());
    }

    public List<Map<String, Object>> emailReport(String school, String session, String entityType, String status, LocalDate from, LocalDate to) {
        StringBuilder sql = new StringBuilder("SELECT entitytype,created_date,ReceipientEmailId,SenderEmailId,registration_no,EmailStatus,EmailStatusDate,inst_no,EmailSubject,EmailContent FROM EmailReport WITH (NOLOCK) WHERE school_id=? AND (?='' OR session=?)");
        List<Object> args = new ArrayList<>(List.of(school, session, session));
        if (!blank(entityType) && !"0".equals(entityType)) { sql.append(" AND EntityType=?"); args.add(entityType); }
        if (!blank(status) && !"0".equals(status)) { sql.append(" AND EmailStatus=?"); args.add(status); }
        if (from != null) { sql.append(" AND CAST(created_date AS date)>=?"); args.add(java.sql.Date.valueOf(from)); }
        if (to != null) { sql.append(" AND CAST(created_date AS date)<=?"); args.add(java.sql.Date.valueOf(to)); }
        sql.append(" ORDER BY created_date DESC");
        return jdbc.queryForList(sql.toString(), args.toArray());
    }

    public List<Map<String, Object>> smsReport(String school, String session, LocalDate from, LocalDate to, String status, String userType, String mobile, String classId, String secId) {
        StringBuilder sql = new StringBuilder("SELECT s.userid AS userId,s.smstype AS smsType,s.mobileno AS mobileNo,s.status AS status,COALESCE(NULLIF(s.smscount,0),CEILING(CAST(LEN(COALESCE(s.message,'')) AS float)/160.0)) AS smsCount,s.message AS message,CONVERT(varchar,CAST(s.[date] AS datetime),100) AS sendDateTime,s.[date] AS sentDate FROM sms_report s WITH (NOLOCK) WHERE s.school_id=?");
        List<Object> args = new ArrayList<>(List.of(school));
        if (from != null) { sql.append(" AND CAST(s.[date] AS date)>=?"); args.add(java.sql.Date.valueOf(from)); }
        if (to != null) { sql.append(" AND CAST(s.[date] AS date)<=?"); args.add(java.sql.Date.valueOf(to)); }
        if (!blank(status) && !"0".equals(status)) { sql.append(" AND s.status=?"); args.add(status); }
        if (!blank(userType) && !"0".equals(userType)) { sql.append(" AND s.smstype=?"); args.add(userType); }
        if (!blank(mobile)) { sql.append(" AND s.mobileno=?"); args.add(mobile); }
        sql.append(" ORDER BY s.[date] DESC");
        List<Map<String, Object>> raw = jdbc.queryForList(sql.toString(), args.toArray());
        if (raw.isEmpty()) return List.of();
        Map<String, Map<String, Object>> students = new HashMap<>();
        Map<String, Map<String, Object>> faculties = new HashMap<>();
        List<String> studentIds = raw.stream().filter(r -> "S".equalsIgnoreCase(text(r.get("smsType")))).map(r -> text(r.get("userId"))).filter(v -> !v.isBlank()).distinct().toList();
        List<String> facultyIds = raw.stream().filter(r -> "F".equalsIgnoreCase(text(r.get("smsType")))).map(r -> text(r.get("userId"))).filter(v -> !v.isBlank()).distinct().toList();
        loadPeople(school, session, studentIds, students);
        loadFaculty(school, facultyIds, faculties);
        List<Map<String, Object>> out = new ArrayList<>();
        for (Map<String, Object> row : raw) {
            String kind = text(row.get("smsType")); String id = text(row.get("userId")); Map<String, Object> person = "S".equalsIgnoreCase(kind) ? students.get(id) : faculties.get(id);
            if (!blank(classId) && !"0".equals(classId) && (person == null || !classId.equals(text(person.get("classId"))))) continue;
            if (!blank(secId) && !"0".equals(secId) && (person == null || !secId.equals(text(person.get("secId"))))) continue;
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("UserId", "S".equalsIgnoreCase(kind) ? id : ("F".equalsIgnoreCase(kind) ? "Faculty" : "Individual SMS"));
            item.put("UserName", person == null ? ("F".equalsIgnoreCase(kind) ? "Faculty" : "Individual SMS") : person.get("name"));
            item.put("UserCategory", "S".equalsIgnoreCase(kind) ? "Student" : ("F".equalsIgnoreCase(kind) ? "Faculty" : "Individual SMS"));
            item.put("cls", person == null ? "" : person.getOrDefault("className", "")); item.put("MobileNo", row.get("mobileNo")); item.put("Status", row.get("status")); item.put("SMSCount", row.get("smsCount")); item.put("Message", row.get("message")); item.put("SendDateTime", row.get("sendDateTime")); out.add(item);
        }
        return out;
    }

    /**
     * Fee_notice.aspx report source expressed as a parameterised query.  The
     * legacy page asks the fee-defaulter report for all installments up to the
     * selected installment; the ledger already contains the same calculated
     * charges, receipts and balances, so this query keeps that behaviour
     * without invoking a database routine.
     */
    public List<Map<String, Object>> feeNotice(String school, String session, int feeType, int installment,
                                                String classId, String admissionNo, double minimumBalance,
                                                String orderBy, boolean includeLateFee) {
        StringBuilder sql = new StringBuilder("SELECT sm.stu_id AS studentId,sm.registration_no AS admNo,UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'')))) AS studentName,COALESCE(so.father_name,'') AS fatherName,COALESCE(so.mobile,'') AS mobile,COALESCE(so.student_E_mail,'') AS emailId,COALESCE(cn.class_name,'')+' ('+COALESCE(tc.section,'')+')' AS className,l.installment_no AS installmentNo,MAX(COALESCE(im.installment_dis,'')) AS installmentName,SUM(COALESCE(l.head_amount,0)) AS charges,SUM(COALESCE(l.concession,0)) AS concession,CAST(0 AS decimal(18,2)) AS waveOff,SUM(COALESCE(l.receive_amount,0)) AS receiveAmount,SUM(COALESCE(l.balance,0)) AS balance,COALESCE(MAX(late.lateFee),0) AS lateFee,MAX(CONVERT(varchar(20),im.due_date,106)) AS dueDate FROM fee_student_ledger l JOIN TB_StudentMaster sm ON sm.school_id=l.school_id AND sm.stu_id=l.stu_id AND sm.session=l.session JOIN student_other_info so ON so.school_id=sm.school_id AND so.uid=sm.uid AND so.registration_no=sm.registration_no LEFT JOIN class_name cn ON cn.school_id=sm.school_id AND cn.id=sm.class_id LEFT JOIN TB_Class tc ON tc.school_id=sm.school_id AND tc.sec_id=sm.sec_id AND tc.class_id=sm.class_id LEFT JOIN Installment_Master im ON im.school_id=l.school_id AND im.session=l.session AND im.installment_no=l.installment_no AND im.fee_type_id=l.fee_type_id LEFT JOIN (SELECT school_id,session,stu_id,inst_no,SUM(COALESCE(late_fee,0)) AS lateFee FROM stu_fee_deposit WHERE LOWER(status)='active' GROUP BY school_id,session,stu_id,inst_no) late ON late.school_id=l.school_id AND late.session=l.session AND late.stu_id=l.stu_id AND late.inst_no=l.installment_no WHERE l.school_id=? AND l.session=? AND LOWER(sm.status)='registered' AND COALESCE(l.balance,0)>0");
        List<Object> args = new ArrayList<>(List.of(school, session));
        if (feeType == 6 || feeType == 7) { sql.append(" AND l.fee_type_id=?"); args.add(feeType); }
        else sql.append(" AND l.fee_type_id NOT IN (6,7)");
        if (installment > 0) { sql.append(" AND l.installment_no<=?"); args.add(installment); }
        if (classId != null && !classId.isBlank() && !"0".equals(classId)) { sql.append(" AND sm.sec_id=?"); args.add(classId); }
        if (admissionNo != null && !admissionNo.isBlank()) { sql.append(" AND sm.registration_no=?"); args.add(admissionNo.trim()); }
        sql.append(" GROUP BY sm.stu_id,sm.registration_no,so.first_name,so.mid_name,so.last_name,so.father_name,so.mobile,so.student_E_mail,cn.class_name,tc.section,l.installment_no");
        sql.append(" HAVING SUM(COALESCE(l.balance,0))>=?"); args.add(minimumBalance);
        if ("balance desc".equalsIgnoreCase(orderBy)) sql.append(" ORDER BY balance DESC,studentName");
        else if ("name".equalsIgnoreCase(orderBy)) sql.append(" ORDER BY studentName");
        else sql.append(" ORDER BY balance,studentName");
        List<Map<String, Object>> rows = jdbc.queryForList(sql.toString(), args.toArray());
        if (!includeLateFee) rows.forEach(r -> r.put("lateFee", 0));
        return rows;
    }

    public int queueFeeNoticeEmails(String school, String staff, String session, String installment, String subject,
                                    String body, List<String> admissionNos) {
        if (admissionNos.isEmpty()) return 0;
        String marks = String.join(",", Collections.nCopies(admissionNos.size(), "?"));
        List<Object> args = new ArrayList<>(List.of(school, session)); args.addAll(admissionNos);
        List<Map<String, Object>> students = jdbc.queryForList("SELECT uid,registration_no AS admissionNo,COALESCE(student_E_mail,'') AS emailId FROM student_other_info WHERE school_id=? AND registration_no IN ("+marks+")", args.toArray());
        int count = 0;
        for (Map<String, Object> student : students) {
            String email = text(student.get("emailId"));
            if (email.isBlank()) continue;
            insertEmailReport(school, session, text(student.get("uid")), text(student.get("admissionNo")), "FeeNotice", body, installment, "", "Pending", "", staff, email, "", subject);
            count++;
        }
        return count;
    }

    private void loadPeople(String school, String session, List<String> ids, Map<String, Map<String, Object>> target) {
        for (List<String> chunk : chunks(ids, 500)) {
            if (chunk.isEmpty()) continue; String marks = String.join(",", Collections.nCopies(chunk.size(), "?")); List<Object> p = new ArrayList<>(List.of(school, session)); p.addAll(chunk);
            for (Map<String, Object> row : jdbc.queryForList("SELECT sm.registration_no AS registrationNo,sm.class_id AS classId,sm.sec_id AS secId,UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'')))) AS name,COALESCE(cn.class_name,'')+'('+COALESCE(tc.section,'')+')' AS className FROM TB_StudentMaster sm LEFT JOIN student_other_info so ON so.school_id=sm.school_id AND so.registration_no=sm.registration_no AND so.uid=sm.uid LEFT JOIN TB_Class tc ON tc.school_id=sm.school_id AND tc.sec_id=sm.sec_id LEFT JOIN class_name cn ON cn.school_id=sm.school_id AND cn.id=sm.class_id WHERE sm.school_id=? AND sm.session=? AND sm.registration_no IN ("+marks+")", p.toArray())) target.put(text(row.get("registrationNo")), row);
        }
    }

    private void loadFaculty(String school, List<String> ids, Map<String, Map<String, Object>> target) {
        for (List<String> chunk : chunks(ids, 500)) {
            if (chunk.isEmpty()) continue; String marks = String.join(",", Collections.nCopies(chunk.size(), "?")); List<Object> p = new ArrayList<>(List.of(school)); p.addAll(chunk);
            for (Map<String, Object> row : jdbc.queryForList("SELECT CAST(f_id AS varchar(50)) AS facultyId,UPPER(LTRIM(RTRIM(COALESCE(first_name,'')+' '+COALESCE(mid_name,'')+' '+COALESCE(last_name,'')))) AS name FROM TB_FacultyMaster WHERE school_id=? AND f_id IN ("+marks+")", p.toArray())) target.put(text(row.get("facultyId")), row);
        }
    }

    private static <T> List<List<T>> chunks(List<T> values, int size) { List<List<T>> out = new ArrayList<>(); for (int i=0; i<values.size(); i+=size) out.add(values.subList(i, Math.min(i+size, values.size()))); return out; }

    public String insertSms(String school, String staff, String guid, String templateId, String userId, String type, String mobile, String message) {
        jdbc.update("INSERT INTO sms_report(newguid,temp_id,sms_id,userid,smstype,mobileno,message,[date],status,school_id,created_by,created_date,smscount) VALUES(?,?,?,?,?,?,?,GETDATE(),'Not Delivered',?,?,GETDATE(),?)", guid, templateId, "Not Found", userId, type, mobile, message, school, staff, smsParts(message));
        return guid;
    }

    public void updateSms(String school, String guid, String smsId, String response, String responseCode) {
        jdbc.update("UPDATE sms_report SET sms_id=?,response=?,responseCode=?,smscount=? WHERE school_id=? AND newguid=?", smsId, response, responseCode, 1, school, guid);
    }

    public void insertEmailReport(String school, String session, String studentId, String admissionNo, String entityType, String content, String installment, String responseId, String status, String statusDate, String staff, String recipient, String sender, String subject) {
        jdbc.update("INSERT INTO EmailReport(school_id,session,stu_id,registration_no,EntityType,EmailContent,inst_no,ResponseId,EmailStatus,EmailStatusDate,created_by,ReceipientEmailId,SenderEmailId,EmailSubject,created_date) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,GETDATE())", school, session, studentId, admissionNo, entityType, content, installment, responseId, status, statusDate, staff, recipient, sender, subject);
    }

    private static int smsParts(String message) { int n = message == null ? 0 : message.length(); return Math.max(1, (n + 159) / 160); }
    private static boolean blank(Object v) { return v == null || text(v).isBlank(); }
    private static String text(Object v) { return v == null ? "" : String.valueOf(v).trim(); }
}
