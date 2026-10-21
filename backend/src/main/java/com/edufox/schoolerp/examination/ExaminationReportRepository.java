package com.edufox.schoolerp.examination;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Direct SQL read/write layer for Exam Master reports.
 *
 * The ASPX pages use many report procedures and ReportViewer datasets.  The
 * React migration keeps the same filter/data relationships here, but queries
 * the underlying tables directly so the new backend never executes a stored
 * procedure.
 */
@Repository
@RequiredArgsConstructor
public class ExaminationReportRepository {
    private final JdbcTemplate jdbc;
    private final ExaminationWorkRepository work;

    public Map<String, Object> bootstrap(String school, String staffUid) {
        String session = work.defaultSession(school);
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("session", session);
        out.put("sessions", safe("SELECT session,status FROM TB_Session WHERE school_id=? ORDER BY id DESC", school));
        out.put("classes", work.classes(school, session, staffUid));
        out.put("classGroups", safe("SELECT id AS id,class_name AS name,class_group AS classGroup FROM class_name WHERE school_id=? ORDER BY id", school));
        out.put("terms", work.terms(school, session, null));
        out.put("subjects", safe("SELECT id AS id,Subject AS name,Subject_code AS code FROM TB_SubjectMaster WHERE school_id=? AND (status IS NULL OR status=1) ORDER BY Subject", school));
        out.put("examTypes", safe("SELECT DISTINCT exam_type_id AS id,exam_type AS name FROM exam_type WHERE school_id=? ORDER BY exam_type_id", school));
        out.put("annualReports", annualReports(school));
        out.put("streams", safe("SELECT DISTINCT stream AS id,stream AS name FROM TB_StudentMaster WHERE school_id=? AND NULLIF(LTRIM(RTRIM(stream)),'') IS NOT NULL ORDER BY stream", school));
        return out;
    }

    public Map<String, Object> options(String school, String session, int sectionId, int classId, String termId) {
        Map<String, Object> out = new LinkedHashMap<>();
        int resolvedClass = classId > 0 ? classId : scalarInt("SELECT TOP 1 class_id FROM TB_Class WHERE school_id=? AND sec_id=?", school, sectionId);
        out.put("sections", sections(school, resolvedClass));
        out.put("classGroups", safe("SELECT id AS id,class_name AS name,class_group AS classGroup FROM class_name WHERE school_id=? ORDER BY id", school));
        out.put("terms", work.terms(school, session, resolvedClass));
        out.put("subjects", sectionId > 0 ? work.subjects(school, session, sectionId, "") : safe("SELECT id AS id,Subject AS name,Subject_code AS code FROM TB_SubjectMaster WHERE school_id=? ORDER BY Subject", school));
        out.put("exams", sectionId > 0 && termId != null && !termId.isBlank() ? work.exams(school, session, sectionId, termId) : safe("SELECT DISTINCT exam_type_id AS id,exam_type AS name FROM exam_type WHERE school_id=? ORDER BY exam_type_id", school));
        out.put("students", students(school, session, sectionId));
        out.put("headers", sectionId > 0 ? work.preprimaryHeaders(school, session, sectionId) : List.of());
        out.put("annualReports", annualReports(school));
        return out;
    }

    public Map<String, Object> data(String school, Map<String, Object> request) {
        String report = text(request.get("report"));
        String session = text(request.get("session"));
        int sectionId = integer(request.get("sectionId"));
        int classId = integer(request.get("classId"));
        String term = text(request.get("termId"));
        String exam = text(request.get("examTypeId"));
        String subject = text(request.get("subjectId"));
        String student = text(request.get("studentId"));
        if (student.isBlank()) student = text(request.get("student"));
        if ("subjects".equalsIgnoreCase(report)) {
            List<Map<String,Object>> catalog = subjectCatalog(school, session);
            Map<String,Object> out = new LinkedHashMap<>();
            out.put("columns", columns(report, catalog)); out.put("rows", catalog);
            out.put("meta", Map.of("report", report, "session", session, "count", catalog.size()));
            return out;
        }
        if ("reportcard".equalsIgnoreCase(report) || "reportcard-examwise".equalsIgnoreCase(report)) {
            Map<String, Object> reportCard = reportCardData(school, session, sectionId, classId, term, exam,
                student, text(request.get("type")), text(request.get("date")),
                "reportcard-examwise".equalsIgnoreCase(report));
            List<Map<String, Object>> reportRows = listValue(reportCard.get("subjects"));
            Map<String, Object> out = new LinkedHashMap<>();
            out.put("columns", columns(report, reportRows));
            out.put("rows", reportRows);
            out.put("reportCard", reportCard);
            out.put("meta", Map.of("report", report, "session", session, "count", reportRows.size()));
            return out;
        }
        List<Map<String, Object>> exact = exactRows(school, request, report, session, sectionId, classId, term, exam, subject, student);
        List<Map<String, Object>> base = exact == null ? marksRows(school, session, sectionId, classId, term, exam, subject, student) : exact;
        List<Map<String, Object>> rows = exact == null ? shape(report, base, request) : base;
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("columns", columns(report, rows));
        out.put("rows", rows);
        out.put("meta", Map.of("report", report, "session", session, "count", rows.size()));
        return out;
    }

    public List<Map<String, Object>> annualReports(String school) {
        return safe("SELECT id,title,file_path AS filePath,c_date AS reportDate FROM cbse_annual_report WHERE school_id=? AND status=1 ORDER BY c_date DESC,id DESC", school);
    }

    private List<Map<String,Object>> subjectCatalog(String school, String session) {
        return safe("""
            WITH aa AS (
              SELECT cs.class_id,cs.subject_code,cs.subject_order,cs.subject_id,ss.sub_subject_id
              FROM TB_ClassSubject cs
              LEFT JOIN SubSubject ss ON ss.school_id=cs.school_id AND ss.subject_id=cs.subject_id AND ss.class_id=cs.class_id
              WHERE cs.school_id=? AND cs.session=? AND cs.status=1
            ), ac AS (
              SELECT * FROM aa a WHERE a.subject_id NOT IN (SELECT ISNULL(a1.sub_subject_id,'') FROM aa a1 WHERE a1.class_id=a.class_id)
            ), pract AS (
              SELECT subject_id,Pr_subject_id,sec_id FROM tb_pract_subject WHERE school_id=? AND session=?
            )
            SELECT a.subject_id AS subject_id,a.class_id AS class_id,a.subject_order AS subject_order,
              s.Subject AS subject,s.Subject_code AS subject_code,
              STRING_AGG(s1.Subject,', ') AS subsubject,pr.Pr_subject_id,s2.Subject AS pr_Subject,
              ISNULL(cn.class_name,(SELECT TOP 1 cn2.class_name+' ('+c.section+')' FROM tb_class c
                JOIN class_name cn2 ON cn2.school_id=c.school_id AND cn2.id=c.class_id
                WHERE c.school_id=? AND c.sec_id=a.class_id)) AS class
            FROM ac a
            LEFT JOIN TB_SubjectMaster s ON s.school_id=? AND s.id=a.subject_id
            LEFT JOIN pract pr ON pr.subject_id=a.subject_id AND a.class_id=pr.sec_id
            LEFT JOIN TB_SubjectMaster s1 ON s1.school_id=? AND s1.id=ISNULL(a.sub_subject_id,'')
            LEFT JOIN TB_SubjectMaster s2 ON s2.school_id=? AND s2.id=ISNULL(pr.Pr_subject_id,'')
            LEFT JOIN class_name cn ON cn.school_id=? AND cn.id=a.class_id
            WHERE s.school_id=? AND a.subject_id NOT IN (SELECT p.Pr_subject_id FROM pract p WHERE p.sec_id=a.class_id)
            GROUP BY a.subject_id,a.class_id,a.subject_order,s.Subject,s.Subject_code,pr.Pr_subject_id,s2.Subject,cn.class_name
            ORDER BY a.class_id,s.Subject
            """, school, session, school, session, school, school, school, school, school, school);
    }

    public Map<String, Object> saveAnnualReport(String school, String staffUid, String title, String date, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("Select a file");
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Title is required");
        String clean = file.getOriginalFilename() == null ? "report" : file.getOriginalFilename().replaceAll("[^A-Za-z0-9._-]", "_");
        Path root = Path.of("uploads", "exam-reports");
        Files.createDirectories(root);
        String stored = UUID.randomUUID() + "_" + clean;
        Files.copy(file.getInputStream(), root.resolve(stored), StandardCopyOption.REPLACE_EXISTING);
        String reportDate = date == null || date.isBlank() ? LocalDate.now().toString() : date;
        jdbc.update("INSERT INTO cbse_annual_report(title,file_path,c_date,created_by,modified_date,modified_by,school_id,status) VALUES(?,?,?,?,?,?,?,1)", title.trim(), stored, reportDate, staffUid, null, null, school);
        return Map.of("message", "Record saved successfully", "reports", annualReports(school));
    }

    public Map<String, Object> deleteAnnualReport(String school, String id) {
        jdbc.update("UPDATE cbse_annual_report SET status=0 WHERE school_id=? AND id=?", school, id);
        return Map.of("message", "Record deleted successfully", "reports", annualReports(school));
    }

    private List<Map<String, Object>> sections(String school, int classId) {
        if (classId <= 0) return List.of();
        return safe("SELECT sec_id AS id,section AS name FROM TB_Class WHERE school_id=? AND class_id=? ORDER BY sec_id", school, classId);
    }

    private List<Map<String, Object>> students(String school, String session, int sectionId) {
        String section = sectionId > 0 ? " AND s.sec_id=?" : "";
        List<Object> args = new ArrayList<>(List.of(school, session));
        if (sectionId > 0) args.add(sectionId);
        return safe("SELECT s.stu_id AS id,s.registration_no AS registrationNo,UPPER(REPLACE(COALESCE(o.first_name,'')+' '+COALESCE(o.mid_name,'')+' '+COALESCE(o.last_name,''),'  ',' ')) AS name,s.sec_id AS sectionId FROM TB_StudentMaster s LEFT JOIN student_other_info o ON o.school_id=s.school_id AND o.registration_no=s.registration_no WHERE s.school_id=? AND s.session=? AND LOWER(s.status)='registered'" + section + " ORDER BY name", args.toArray());
    }

    private List<Map<String, Object>> marksRows(String school, String session, int sectionId, int classId, String term, String exam, String subject, String student) {
        return safe("""
            SELECT s.stu_id AS studentId,s.registration_no AS registrationNo,
              UPPER(REPLACE(COALESCE(o.first_name,'')+' '+COALESCE(o.mid_name,'')+' '+COALESCE(o.last_name,''),'  ',' ')) AS studentName,
              s.sec_id AS sectionId,c.class_id AS classId,c.section AS sectionName,cn.class_name AS className,cn.class_group AS classGroup,
              sm.subject_id AS subjectId,sub.Subject AS subjectName,sm.exam_type_id AS examTypeId,et.exam_type AS examName,sm.term_id AS termId,
              COALESCE(CONVERT(varchar(40),sm.marks),'') AS marks,COALESCE(CONVERT(varchar(40),sm.min_marks),'') AS minMarks,
              COALESCE(CONVERT(varchar(40),sm.max_marks),'') AS maxMarks,COALESCE(sm.absentism,'') AS absentism,
              COALESCE(CONVERT(varchar(40),sm.Weightage_Marks),'') AS weightageMarks,COALESCE(CONVERT(varchar(40),sm.calculated_marks),'') AS calculatedMarks,
              COALESCE(sm.created_by,'') AS createdBy,sm.created_date AS createdDate
            FROM TB_StudentMaster s
            LEFT JOIN student_other_info o ON o.school_id=s.school_id AND o.registration_no=s.registration_no
            JOIN TB_Class c ON c.school_id=s.school_id AND c.sec_id=s.sec_id
            JOIN class_name cn ON cn.school_id=c.school_id AND cn.id=c.class_id
            LEFT JOIN senior_marks sm ON sm.school_id=s.school_id AND sm.stu_id=s.stu_id AND sm.session=s.session
              AND sm.sec_id=s.sec_id AND (?='' OR CONVERT(varchar(50),sm.term_id)=?)
              AND (?='' OR CONVERT(varchar(50),sm.exam_type_id)=?) AND (?='' OR CONVERT(varchar(50),sm.subject_id)=?)
            LEFT JOIN TB_SubjectMaster sub ON sub.school_id=s.school_id AND sub.id=sm.subject_id
            LEFT JOIN exam_type et ON et.school_id=s.school_id AND et.exam_type_id=sm.exam_type_id
            WHERE s.school_id=? AND s.session=? AND LOWER(s.status)='registered'
              AND (?=0 OR s.sec_id=?)
              AND (?=0 OR s.sec_id IN (SELECT sec_id FROM TB_Class WHERE school_id=? AND class_id=?))
              AND (?='' OR CONVERT(varchar(50),s.stu_id)=?)
            ORDER BY studentName,subjectName,examName
            """, term, term, exam, exam, subject, subject, school, session, sectionId, sectionId, classId, school, classId, student, student);
    }

    /**
     * Report-specific direct SQL equivalents for the report procedures used by
     * the supplied ASPX pages.  This is deliberately kept separate from the
     * generic marks query: several legacy reports return a different dataset
     * shape (for example the marks log and class-subject report).
     */
    private List<Map<String, Object>> exactRows(String school, Map<String, Object> request, String report,
                                                 String session, int sectionId, int classId, String term,
                                                 String exam, String subject, String student) {
        return switch (report.toLowerCase()) {
            case "subject-report" -> classSubjectRows(school, session, sectionId, classId);
            case "marks-status-graph" -> marksStatusSubjectRows(school, session, sectionId, exam, subject);
            case "marks-log" -> marksLogRows(school, session, sectionId, exam, subject);
            case "mark-status" -> markStatusRows(school, session, sectionId, classId, exam);
            case "percentage" -> percentageRows(school, session, sectionId, term, exam, subject, request);
            case "failure" -> failureRows(school, session, sectionId, classId, term, exam, subject, request);
            case "cbse-overall", "cbse-main" -> cbseRows(school, session, sectionId, classId, request);
            case "overall", "class-overall" -> overallRows(school, session, sectionId, classId);
            case "expected-school" -> expectedSchoolRows(school, session, classId, term, exam);
            case "expected-board" -> expectedBoardRows(school, session, sectionId, classId, term, exam, request);
            case "reportcard" -> reportCardRows(school, session, sectionId, term, "", student);
            case "reportcard-examwise" -> reportCardRows(school, session, sectionId, term, exam, student);
            case "academic-performance" -> academicRows(school, session, sectionId, classId, student);
            case "comparative" -> comparativeRows(school, session, sectionId, classId, request);
            case "cumulative", "examwise-greensheet",
                 "green-sheet", "green-primary", "green-subject",
                 "result-discussion" -> aggregateReportRows(school, session, sectionId, classId, term, exam, subject, student, report, request);
            case "toppers" -> topperReportRows(school, session, sectionId, classId, term, request);
            case "marks-list-blank", "marks-list-junior" -> blankMarksRows(school, session, sectionId, exam, subject);
            default -> null;
        };
    }

    private List<Map<String, Object>> classSubjectRows(String school, String session, int sectionId, int classId) {
        return safe("""
            SELECT s.Subject AS Subject,s.Subject_code AS Subject_code,
              cs.isoptional,cs.isgrade,CASE WHEN cs.isprac='Y' THEN '1' ELSE '0' END AS isprac,
              cs.subject_order
            FROM TB_ClassSubject cs
            JOIN TB_SubjectMaster s ON s.school_id=cs.school_id AND s.id=cs.subject_id
            WHERE cs.school_id=? AND cs.session=? AND cs.status=1
              AND cs.class_id=CASE WHEN ?=0 THEN ? ELSE ? END
            ORDER BY s.Subject
            """, school, session, sectionId, classId, sectionId);
    }

    private List<Map<String, Object>> marksStatusSubjectRows(String school, String session, int sectionId, String exam, String subject) {
        return safe("""
            SELECT s.sec_id,s.stu_id,
              COALESCE(o.first_name,'') AS stu_name,s.subject_id,sub.Subject AS subject_name,
              s.exam_type_id,s.session,
              CASE WHEN ISNUMERIC(s.marks)=1 THEN s.marks ELSE '0' END AS marks,s.max_marks
            FROM senior_marks s
            JOIN TB_StudentMaster sm ON sm.school_id=s.school_id AND sm.session=s.session AND sm.stu_id=s.stu_id
            LEFT JOIN student_other_info o ON o.school_id=sm.school_id AND o.registration_no=sm.registration_no
            LEFT JOIN TB_SubjectMaster sub ON sub.school_id=s.school_id AND sub.id=s.subject_id
            WHERE s.school_id=? AND s.session=?
              AND (?=0 OR s.sec_id=?)
              AND (?='' OR CONVERT(varchar(50),s.exam_type_id)=?)
              AND (?='' OR CONVERT(varchar(50),s.subject_id)=?)
            ORDER BY stu_name
            """, school, session, sectionId, sectionId, exam, exam, subject, subject);
    }

    private List<Map<String, Object>> marksLogRows(String school, String session, int sectionId, String exam, String subject) {
        return safe("""
            WITH current_marks AS (
              SELECT '1' AS sno,s.stu_id,
                CASE WHEN NULLIF(s.absentism,'') IS NOT NULL THEN s.absentism ELSE CONVERT(varchar(50),s.marks) END AS marks,
                s.max_marks,CONVERT(varchar(100),s.created_date) AS created_date,s.created_by,s.f_id,
                CONCAT(COALESCE(f1.first_name,''),' ',COALESCE(f1.mid_name,''),' ',COALESCE(f1.last_name,'')) AS facultyname,
                CONCAT(COALESCE(f.first_name,''),' ',COALESCE(f.mid_name,''),' ',COALESCE(f.last_name,'')) AS loginfacultyname,
                sm.registration_no,sf.first_name+' '+COALESCE(sf.mid_name,'')+' '+COALESCE(sf.last_name,'') AS studentname,
                '' AS deletedby_facultyname,'' AS deleted_date,0 AS change_status
              FROM senior_marks s
              JOIN TB_StudentMaster sm ON sm.school_id=s.school_id AND sm.session=s.session AND sm.stu_id=s.stu_id
              LEFT JOIN student_other_info sf ON sf.school_id=sm.school_id AND sf.registration_no=sm.registration_no
              LEFT JOIN tb_facultymaster f ON f.school_id=s.school_id AND f.staff_u_id=s.created_by
              LEFT JOIN tb_facultymaster f1 ON f1.school_id=s.school_id AND f1.f_id=s.f_id
              WHERE s.school_id=? AND s.session=? AND s.sec_id=? AND s.exam_type_id=? AND s.subject_id=?
            ), deleted_marks AS (
              SELECT '2' AS sno,l.stu_id,
                CASE WHEN NULLIF(l.absentism,'') IS NOT NULL THEN l.absentism ELSE CONVERT(varchar(50),l.marks) END AS marks,
                l.max_marks,CONVERT(varchar(100),l.created_date) AS created_date,l.deleted_by AS created_by,l.f_id,
                CONCAT(COALESCE(f1.first_name,''),' ',COALESCE(f1.mid_name,''),' ',COALESCE(f1.last_name,'')) AS facultyname,
                CONCAT(COALESCE(f2.first_name,''),' ',COALESCE(f2.mid_name,''),' ',COALESCE(f2.last_name,'')) AS loginfacultyname,
                sm.registration_no,sf.first_name+' '+COALESCE(sf.mid_name,'')+' '+COALESCE(sf.last_name,'') AS studentname,
                CONCAT(COALESCE(fd.first_name,''),' ',COALESCE(fd.mid_name,''),' ',COALESCE(fd.last_name,'')) AS deletedby_facultyname,
                CONVERT(varchar(100),l.deleted_date) AS deleted_date,1 AS change_status
              FROM senior_marks_log l
              JOIN TB_StudentMaster sm ON sm.school_id=l.school_id AND sm.session=l.session AND sm.stu_id=l.stu_id
              LEFT JOIN student_other_info sf ON sf.school_id=sm.school_id AND sf.registration_no=sm.registration_no
              LEFT JOIN tb_facultymaster f1 ON f1.school_id=l.school_id AND f1.f_id=l.f_id
              LEFT JOIN tb_facultymaster f2 ON f2.school_id=l.school_id AND f2.staff_u_id=l.created_by
              LEFT JOIN tb_facultymaster fd ON fd.school_id=l.school_id AND fd.staff_u_id=l.deleted_by
              WHERE l.school_id=? AND l.session=? AND sm.sec_id=? AND l.exam_type_id=? AND l.subject_id=?
            )
            SELECT * FROM current_marks UNION ALL SELECT * FROM deleted_marks ORDER BY stu_id,sno
            """, school, session, sectionId, exam, subject, school, session, sectionId, exam, subject);
    }

    private List<Map<String, Object>> percentageRows(String school, String session, int sectionId, String term,
                                                      String exam, String subject, Map<String, Object> request) {
        String from = text(request.get("fromPercent"));
        String to = text(request.get("toPercent"));
        return safe("""
            WITH aa AS (
              SELECT s.stu_id,sm.registration_no AS reg_no,
                UPPER(REPLACE(COALESCE(o.first_name,'')+' '+COALESCE(o.mid_name,'')+' '+COALESCE(o.last_name,''),'  ',' ')) AS name,
                CAST(COALESCE(TRY_CONVERT(decimal(18,2),s.marks),0) /
                  CASE WHEN COALESCE(TRY_CONVERT(decimal(18,2),s.max_marks),0)=0 THEN 1
                       ELSE TRY_CONVERT(decimal(18,2),s.max_marks) END * 100 AS decimal(18,2)) AS per,
                cn.class_name+' ('+c.section+')' AS class_name
              FROM senior_marks s
              JOIN TB_StudentMaster sm ON sm.school_id=s.school_id AND sm.session=s.session AND sm.stu_id=s.stu_id
              LEFT JOIN student_other_info o ON o.school_id=sm.school_id AND o.registration_no=sm.registration_no
              LEFT JOIN TB_Class c ON c.school_id=sm.school_id AND c.sec_id=sm.sec_id
              LEFT JOIN class_name cn ON cn.school_id=c.school_id AND cn.id=c.class_id
              WHERE s.school_id=? AND s.session=? AND LOWER(sm.status)='registered'
                AND (?='' OR CONVERT(varchar(50),s.term_id)=?)
                AND (?='' OR CONVERT(varchar(50),s.exam_type_id)=?)
                AND (?='' OR CONVERT(varchar(50),s.subject_id)=?)
                AND (?=0 OR s.sec_id=?)
            )
            SELECT reg_no,name,per,class_name FROM aa
            WHERE (?='' OR per>=TRY_CONVERT(decimal(18,2),?))
              AND (?='' OR per<=TRY_CONVERT(decimal(18,2),?))
            ORDER BY name
            """, school, session, term, term, exam, exam, subject, subject,
            sectionId, sectionId, from, from, to, to);
    }

    private List<Map<String, Object>> failureRows(String school, String session, int sectionId, int classId, String term,
                                                   String exam, String subject, Map<String, Object> request) {
        String classGroup = scalarText("SELECT TOP 1 class_group FROM class_name WHERE school_id=? AND id=?", school, classId);
        if ("Junior".equalsIgnoreCase(classGroup) || "Primary".equalsIgnoreCase(classGroup)) {
            return juniorFailureRows(school, session, sectionId, classId, exam, subject);
        }
        return safe("""
            WITH aa AS (
              SELECT s.stu_id,sm.registration_no,
                UPPER(REPLACE(COALESCE(o.first_name,'')+' '+COALESCE(o.mid_name,'')+' '+COALESCE(o.last_name,''),'  ',' ')) AS name,
                sm.class_id,sm.sec_id,s.subject_id,sub.Subject AS subject,
                SUM(COALESCE(TRY_CONVERT(decimal(18,2),s.marks),0)) AS obtain,
                SUM(COALESCE(TRY_CONVERT(decimal(18,2),s.max_marks),0)) AS outof,
                cn.class_name+' ('+c.section+')' AS class_name,s.session
              FROM senior_marks s
              JOIN TB_StudentMaster sm ON sm.school_id=s.school_id AND sm.session=s.session AND sm.stu_id=s.stu_id
              LEFT JOIN student_other_info o ON o.school_id=sm.school_id AND o.registration_no=sm.registration_no
              LEFT JOIN TB_SubjectMaster sub ON sub.school_id=s.school_id AND sub.id=s.subject_id
              LEFT JOIN TB_Class c ON c.school_id=sm.school_id AND c.sec_id=sm.sec_id
              LEFT JOIN class_name cn ON cn.school_id=c.school_id AND cn.id=c.class_id
              WHERE s.school_id=? AND s.session=? AND LOWER(sm.status)='registered'
                AND (?=0 OR sm.class_id=?) AND (?=0 OR sm.sec_id=?)
                AND (?='' OR CONVERT(varchar(50),s.exam_type_id)=?)
                AND (?='' OR CONVERT(varchar(50),s.subject_id)=?)
              GROUP BY s.stu_id,sm.registration_no,o.first_name,o.mid_name,o.last_name,sm.class_id,sm.sec_id,
                s.subject_id,sub.Subject,cn.class_name,c.section,s.session
            )
            SELECT class_name AS class,subject,name,
              CONVERT(varchar(50),CAST(obtain AS decimal(18,2)))+'/'+CONVERT(varchar(50),CAST(outof AS decimal(18,2))) AS marks,
              class_id,sec_id,subject_id,session
            FROM aa WHERE outof>0 AND obtain*100.0/outof < 33
            ORDER BY class_id,sec_id,subject_id,name
            """, school, session, classId, classId, sectionId, sectionId, exam, exam, subject, subject);
    }

    /** Junior/primary failure reports read the configured exam column from exam_reports. */
    private List<Map<String, Object>> juniorFailureRows(String school, String session, int sectionId, int classId,
                                                        String exam, String subject) {
        String column = scalarText("SELECT TOP 1 column_name FROM exam_reports_column WHERE exam_type_id=? ORDER BY row_id", exam);
        if (!column.matches("[A-Za-z0-9_]+")) return List.of();
        String maxColumn = column + "_MAX";
        String sql = """
            WITH aa AS (
              SELECT c.SUB_ID AS subject_id,c.STU_ID AS stu_id,sm.sec_id,sm.class_id,
                CASE WHEN UPPER(CONVERT(varchar(50),c.[%s])) IN ('ML','NA','OD','AB') THEN CAST(0 AS decimal(18,2))
                     ELSE SUM(COALESCE(TRY_CONVERT(decimal(18,2),c.[%s]),0)) END AS obtain,
                SUM(COALESCE(TRY_CONVERT(decimal(18,2),c.[%s]),0)) AS outof,
                CONVERT(varchar(50),c.[%s]) AS absentism,c.session
              FROM exam_reports c
              JOIN TB_StudentMaster sm ON sm.school_id=c.school_id AND sm.session=c.session AND sm.stu_id=CONVERT(int,c.STU_ID)
              WHERE c.school_id=? AND c.session=? AND LOWER(sm.status)='registered'
                AND (?=0 OR sm.class_id=?) AND (?=0 OR sm.sec_id=?)
                AND (?='' OR CONVERT(varchar(50),c.SUB_ID)=?)
              GROUP BY c.SUB_ID,c.STU_ID,sm.sec_id,sm.class_id,c.[%s],c.session
            )
            SELECT cn.class_name+' ('+tc.section+')' AS class,sub.Subject AS subject,
              UPPER(REPLACE(COALESCE(o.first_name,'')+' '+COALESCE(o.mid_name,'')+' '+COALESCE(o.last_name,''),'  ',' ')) AS name,
              CONVERT(varchar(50),CAST(obtain AS decimal(18,2)))+'/'+CONVERT(varchar(50),CAST(outof AS decimal(18,2))) AS marks,
              class_id,sec_id,subject_id,session
            FROM aa
            JOIN TB_Class tc ON tc.school_id=? AND tc.sec_id=aa.sec_id
            JOIN class_name cn ON cn.school_id=tc.school_id AND cn.id=tc.class_id
            LEFT JOIN TB_SubjectMaster sub ON sub.school_id=? AND sub.id=aa.subject_id
            LEFT JOIN TB_StudentMaster sm ON sm.school_id=? AND sm.stu_id=aa.stu_id AND sm.session=aa.session
            LEFT JOIN student_other_info o ON o.school_id=sm.school_id AND o.registration_no=sm.registration_no
            WHERE outof>0 AND obtain*100.0/outof<40 AND UPPER(COALESCE(absentism,'')) NOT IN ('ML','NA','OD')
            ORDER BY class_id,sec_id,subject_id,name
            """.formatted(column, column, maxColumn, column, column);
        return safe(sql, school, session, classId, classId, sectionId, sectionId, subject, subject,
            school, school, school);
    }

    private List<Map<String, Object>> aggregateReportRows(String school, String session, int sectionId, int classId,
                                                           String term, String exam, String subject, String student,
                                                           String report, Map<String, Object> request) {
        List<Map<String, Object>> rows = safe("""
            WITH aa AS (
              SELECT s.stu_id,sm.registration_no,
                UPPER(REPLACE(COALESCE(o.first_name,'')+' '+COALESCE(o.mid_name,'')+' '+COALESCE(o.last_name,''),'  ',' ')) AS student_name,
                sm.class_id,sm.sec_id,cn.class_name,c.section AS section_name,
                SUM(COALESCE(TRY_CONVERT(decimal(18,2),s.marks),0)) AS total,
                SUM(CASE WHEN COALESCE(TRY_CONVERT(decimal(18,2),s.max_marks),0)=0 THEN 0 ELSE TRY_CONVERT(decimal(18,2),s.max_marks) END) AS max_total,
                COUNT(DISTINCT s.subject_id) AS subjects
              FROM senior_marks s
              JOIN TB_StudentMaster sm ON sm.school_id=s.school_id AND sm.session=s.session AND sm.stu_id=s.stu_id
              LEFT JOIN student_other_info o ON o.school_id=sm.school_id AND o.registration_no=sm.registration_no
              LEFT JOIN TB_Class c ON c.school_id=sm.school_id AND c.sec_id=sm.sec_id
              LEFT JOIN class_name cn ON cn.school_id=c.school_id AND cn.id=c.class_id
              WHERE s.school_id=? AND s.session=? AND LOWER(sm.status)='registered'
                AND (?=0 OR s.sec_id=?) AND (?=0 OR sm.class_id=?)
                AND (?='' OR CONVERT(varchar(50),s.term_id)=?)
                AND (?='' OR CONVERT(varchar(50),s.exam_type_id)=?)
                AND (?='' OR CONVERT(varchar(50),s.subject_id)=?)
                AND (?='' OR CONVERT(varchar(50),s.stu_id)=?)
              GROUP BY s.stu_id,sm.registration_no,o.first_name,o.mid_name,o.last_name,sm.class_id,sm.sec_id,cn.class_name,c.section
            )
            SELECT stu_id AS studentId,registration_no AS registrationNo,student_name AS studentName,
              class_name AS className,section_name AS sectionName,total,max_total AS maxTotal,subjects,
              CAST(CASE WHEN max_total=0 THEN 0 ELSE total*100.0/max_total END AS decimal(18,2)) AS percentage,
              DENSE_RANK() OVER(ORDER BY CASE WHEN max_total=0 THEN 0 ELSE total*100.0/max_total END DESC) AS rank
            FROM aa ORDER BY percentage DESC,student_name
            """, school, session, sectionId, sectionId, classId, classId, term, term, exam, exam,
            subject, subject, student, student);
        if ("academic-performance".equalsIgnoreCase(report)) return rows;
        return rows;
    }

    private List<Map<String, Object>> topperReportRows(String school, String session, int sectionId, int classId,
                                                        String term, Map<String, Object> request) {
        List<Integer> selectedSections = integerList(request.get("sections"));
        if (selectedSections.isEmpty() && classId > 0) {
            selectedSections = sections(school, classId).stream().map(row -> integer(row.get("id"))).filter(id -> id > 0).toList();
        }
        List<Map<String, Object>> rows = new ArrayList<>();
        if (selectedSections.isEmpty()) {
            rows.addAll(aggregateReportRows(school, session, sectionId, classId, term, "", "", "", "toppers", request));
        } else {
            for (Integer selected : selectedSections) {
                rows.addAll(aggregateReportRows(school, session, selected, classId, term, "", "", "", "toppers", request));
            }
            Map<String, Map<String, Object>> unique = new LinkedHashMap<>();
            for (Map<String, Object> row : rows) unique.putIfAbsent(text(row.get("studentId")), row);
            rows = new ArrayList<>(unique.values());
        }
        double minimum = decimal(request.get("percentage"), Double.NEGATIVE_INFINITY);
        rows = rows.stream().filter(row -> decimal(row.get("percentage"), 0) >= minimum).collect(java.util.stream.Collectors.toCollection(ArrayList::new));
        rows.sort((a,b) -> Double.compare(decimal(b.get("percentage"),0), decimal(a.get("percentage"),0)));
        int limit = integer(request.get("topN"));
        boolean sectionWise = "sectionwise".equalsIgnoreCase(text(request.get("type")));
        Map<String, Integer> nextRank = new LinkedHashMap<>();
        for (int i=0;i<rows.size();i++) {
            String key = sectionWise ? text(rows.get(i).get("sectionName")) : "all";
            int rank = nextRank.getOrDefault(key, 0) + 1;
            nextRank.put(key, rank);
            rows.get(i).put("rank", rank);
        }
        return limit > 0 && limit < rows.size() ? new ArrayList<>(rows.subList(0, limit)) : rows;
    }

    private List<Map<String, Object>> blankMarksRows(String school, String session, int sectionId, String exam, String subject) {
        return safe("""
            SELECT ROW_NUMBER() OVER(ORDER BY o.first_name,o.last_name,sm.stu_id) AS srno,
              sm.registration_no,UPPER(REPLACE(COALESCE(o.first_name,'')+' '+COALESCE(o.mid_name,'')+' '+COALESCE(o.last_name,''),'  ',' ')) AS stu_name,
              '' AS marks,'' AS grade,'' AS per,
              (SELECT COUNT(*) FROM TB_StudentMaster x WHERE x.school_id=sm.school_id AND x.session=sm.session AND x.sec_id=sm.sec_id AND LOWER(x.status)='registered') AS total,
              '' AS fail,'' AS absent,'' AS sts
            FROM TB_StudentMaster sm
            LEFT JOIN student_other_info o ON o.school_id=sm.school_id AND o.registration_no=sm.registration_no
            WHERE sm.school_id=? AND sm.session=? AND LOWER(sm.status)='registered' AND (?=0 OR sm.sec_id=?)
            ORDER BY o.first_name,o.last_name,sm.stu_id
            """, school, session, sectionId, sectionId);
    }

    /** Direct equivalent of overall_performance_classwise used by Class_overall_marks. */
    private List<Map<String, Object>> overallRows(String school, String session, int sectionId, int classId) {
        return safe("""
            WITH aa AS (
              SELECT s.stu_id,sm.registration_no,
                UPPER(REPLACE(COALESCE(o.first_name,'')+' '+COALESCE(o.mid_name,'')+' '+COALESCE(o.last_name,''),'  ',' ')) AS name,
                sm.class_id,sm.sec_id,
                SUM(COALESCE(TRY_CONVERT(decimal(18,2),s.marks),0)) AS total_marks,
                SUM(COALESCE(TRY_CONVERT(decimal(18,2),s.max_marks),0)) AS max_marks
              FROM senior_marks s
              JOIN TB_StudentMaster sm ON sm.school_id=s.school_id AND sm.session=s.session AND sm.stu_id=s.stu_id
              LEFT JOIN student_other_info o ON o.school_id=sm.school_id AND o.registration_no=sm.registration_no
              WHERE s.school_id=? AND s.session=? AND LOWER(sm.status)='registered'
                AND (?=0 OR sm.sec_id=?) AND (?=0 OR sm.class_id=?)
              GROUP BY s.stu_id,sm.registration_no,o.first_name,o.mid_name,o.last_name,sm.class_id,sm.sec_id
            ), scored AS (
              SELECT aa.*,
                CAST(CASE WHEN max_marks=0 THEN 0 ELSE total_marks*100.0/max_marks END AS decimal(18,2)) AS per
              FROM aa
            )
            SELECT stu_id,registration_no AS Registration_No,name,per,
              COALESCE((SELECT TOP 1 g.grade FROM chk_grade g
                WHERE g.school_id=? AND g.m_range_from<=scored.per AND g.m_range_to>=scored.per
                ORDER BY g.m_range_from DESC),'') AS Grade,
              CONVERT(varchar(50),CAST(total_marks AS decimal(18,2)))+'/'+CONVERT(varchar(50),CAST(max_marks AS decimal(18,2))) AS total_marks,
              DENSE_RANK() OVER(ORDER BY per DESC) AS Rank
            FROM scored
            ORDER BY per DESC,name
            """, school, session, sectionId, sectionId, classId, classId, school);
    }

    /** Direct equivalent of exam_wise_greensheet_Expected's class-average branch. */
    private List<Map<String, Object>> expectedSchoolRows(String school, String session, int classId,
                                                          String term, String exam) {
        return safe("""
            WITH student_totals AS (
              SELECT sm.stu_id,sm.sec_id,
                SUM(CASE WHEN ISNUMERIC(s.marks)=1 THEN TRY_CONVERT(decimal(18,2),s.marks) ELSE 0 END) AS marks,
                SUM(CASE WHEN ISNUMERIC(s.max_marks)=1 THEN TRY_CONVERT(decimal(18,2),s.max_marks) ELSE 0 END) AS max_marks
              FROM TB_StudentMaster sm
              JOIN senior_marks s ON s.school_id=sm.school_id AND s.session=sm.session AND s.stu_id=sm.stu_id
              WHERE sm.school_id=? AND sm.session=? AND LOWER(sm.status)='registered' AND sm.class_id=?
                AND (?='' OR CONVERT(varchar(50),s.term_id)=?)
                AND (?='' OR CONVERT(varchar(50),s.exam_type_id)=?)
              GROUP BY sm.stu_id,sm.sec_id
            ), section_average AS (
              SELECT sec_id,CAST(AVG(CASE WHEN max_marks=0 THEN 0 ELSE marks*100.0/max_marks END) AS decimal(18,2)) AS avg_per
              FROM student_totals GROUP BY sec_id
            )
            SELECT a.sec_id,a.avg_per,
              cn.class_name+' ('+c.section+')' AS class_name,
              CAST((SELECT AVG(avg_per) FROM section_average) AS decimal(18,2)) AS schoolavg_per
            FROM section_average a
            JOIN TB_Class c ON c.school_id=? AND c.sec_id=a.sec_id
            JOIN class_name cn ON cn.school_id=c.school_id AND cn.id=c.class_id
            ORDER BY a.sec_id
            """, school, session, classId, term, term, exam, exam, school);
    }

    /** Direct SQL report rows for the multi-select subject board-average page. */
    private List<Map<String, Object>> expectedBoardRows(String school, String session, int sectionId, int classId,
                                                         String term, String exam, Map<String, Object> request) {
        List<String> subjects = textList(request.get("subjects"));
        String subjectClause = subjects.isEmpty() ? "" : " AND s.subject_id IN (" + "?,".repeat(subjects.size()).replaceAll(",$", "") + ")";
        List<Object> args = new ArrayList<>(List.of(school, session, term, term, exam, exam, sectionId, sectionId, classId, classId));
        args.addAll(subjects);
        String sql = """
            WITH aa AS (
              SELECT s.subject_id,sub.Subject AS subject_name,s.stu_id,
                TRY_CONVERT(decimal(18,2),s.marks) AS marks,
                NULLIF(TRY_CONVERT(decimal(18,2),s.max_marks),0) AS max_marks
              FROM senior_marks s
              JOIN TB_StudentMaster sm ON sm.school_id=s.school_id AND sm.session=s.session AND sm.stu_id=s.stu_id
              LEFT JOIN TB_SubjectMaster sub ON sub.school_id=s.school_id AND sub.id=s.subject_id
              WHERE s.school_id=? AND s.session=? AND LOWER(sm.status)='registered'
                AND (?='' OR CONVERT(varchar(50),s.term_id)=?)
                AND (?='' OR CONVERT(varchar(50),s.exam_type_id)=?)
                AND (?=0 OR s.sec_id=?) AND (?=0 OR sm.class_id=?)
            """ + subjectClause + """
            ), av AS (
              SELECT subject_id,MAX(subject_name) AS subject_name,
                CAST(AVG(CASE WHEN max_marks IS NULL THEN 0 ELSE marks*100.0/max_marks END) AS decimal(18,2)) AS average_per,
                COUNT(DISTINCT stu_id) AS student_count
              FROM aa WHERE marks IS NOT NULL GROUP BY subject_id
            )
            SELECT subject_id,subject_name,average_per,student_count
            FROM av ORDER BY subject_name
            """;
        return safe(sql, args.toArray());
    }

    /** ReportCard/ReportCardExamwise dataset: one row per student and subject with dynamic exam columns. */
    private List<Map<String, Object>> reportCardRows(String school, String session, int sectionId, String term,
                                                      String exam, String student) {
        StringBuilder sql = new StringBuilder("""
            SELECT s.stu_id,sm.registration_no,
              UPPER(REPLACE(COALESCE(o.first_name,'')+' '+COALESCE(o.mid_name,'')+' '+COALESCE(o.last_name,''),'  ',' ')) AS student_name,
              s.subject_id,sub.Subject AS subject_name,sub.Subject_code AS subject_code,cs.subject_order,
              et.exam_type_id,et.exam_type,
              CASE WHEN NULLIF(LTRIM(RTRIM(s.absentism)),'') IS NOT NULL THEN s.absentism ELSE s.marks END AS marks,
              s.max_marks,s.term_id
            FROM senior_marks s
            JOIN TB_StudentMaster sm ON sm.school_id=s.school_id AND sm.session=s.session AND sm.stu_id=s.stu_id
            LEFT JOIN student_other_info o ON o.school_id=sm.school_id AND o.registration_no=sm.registration_no
            LEFT JOIN TB_SubjectMaster sub ON sub.school_id=s.school_id AND sub.id=s.subject_id
            LEFT JOIN TB_ClassSubject cs ON cs.school_id=s.school_id AND cs.session=s.session AND cs.class_id=sm.class_id AND cs.subject_id=s.subject_id AND cs.status=1
            LEFT JOIN exam_type et ON et.school_id=s.school_id AND et.exam_type_id=TRY_CONVERT(int,s.exam_type_id)
            WHERE s.school_id=CONVERT(varchar(50),?) AND s.session=CONVERT(varchar(50),?) AND LOWER(sm.status)='registered'
            """);
        List<Object> args = new ArrayList<>(List.of(school, session));
        if (sectionId > 0) { sql.append(" AND s.sec_id=CONVERT(varchar(50),?)"); args.add(sectionId); }
        if (term != null && !term.isBlank()) { sql.append(" AND s.term_id=CONVERT(varchar(50),?)"); args.add(term); }
        if (exam != null && !exam.isBlank()) { sql.append(" AND s.exam_type_id=CONVERT(varchar(50),?)"); args.add(exam); }
        if (student != null && !student.isBlank() && !"0".equals(student)) { sql.append(" AND s.stu_id=CONVERT(varchar(50),?)"); args.add(student); }
        sql.append(" ORDER BY student_name,TRY_CONVERT(int,s.subject_id),cs.subject_order,sub.Subject,s.term_id,et.exam_type_id OPTION (RECOMPILE)");
        List<Map<String, Object>> marks = safe(sql.toString(), args.toArray());
        Map<String, Map<String, Object>> grouped = new LinkedHashMap<>();
        for (Map<String, Object> mark : marks) {
            String key = text(mark.get("stu_id")) + ":" + text(mark.get("subject_id"));
            Map<String, Object> row = grouped.computeIfAbsent(key, ignored -> {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("studentId", mark.get("stu_id"));
                item.put("registrationNo", mark.get("registration_no"));
                item.put("studentName", mark.get("student_name"));
                item.put("subjectId", mark.get("subject_id"));
                item.put("subject", mark.get("subject_name"));
                item.put("subjectCode", mark.get("subject_code"));
                item.put("subjectOrder", mark.get("subject_order"));
                item.put("total", 0d);
                item.put("maxTotal", 0d);
                return item;
            });
            String label = text(mark.get("exam_type")).replaceAll("[^A-Za-z0-9]+", "_");
            if (label.isBlank()) label = "Exam_" + text(mark.get("exam_type_id"));
            String value = text(mark.get("marks"));
            row.put(label, value);
            row.put(label + "_max", mark.get("max_marks"));
            double obtained = decimal(value, 0);
            double maximum = decimal(mark.get("max_marks"), 0);
            row.put("total", decimal(row.get("total"), 0) + obtained);
            row.put("maxTotal", decimal(row.get("maxTotal"), 0) + maximum);
        }
        for (Map<String, Object> row : grouped.values()) {
            double max = decimal(row.get("maxTotal"), 0);
            row.put("percentage", max == 0 ? 0 : Math.round(decimal(row.get("total"), 0) * 10000 / max) / 100d);
        }
        return new ArrayList<>(grouped.values());
    }

    /**
     * The legacy ReportCard page is a composed report, not a plain marks table.
     * ReportCard_StudentList supplies the student/school header, the selected
     * marks procedure supplies the scholastic rows, and the subreport supplies
     * attendance, health, indicators and remarks.  This is the direct-table
     * equivalent of that composition; it intentionally does not execute any
     * of those legacy procedures.
     */
    private Map<String, Object> reportCardData(String school, String session, int sectionId, int classId,
                                                String term, String exam, String student, String type,
                                                String date, boolean examWise) {
        Map<String, Object> out = new LinkedHashMap<>();
        List<Map<String, Object>> subjects = reportCardRows(school, session, sectionId, term,
            examWise ? exam : "", student);
        Map<String, Object> profile = reportCardStudent(school, session, sectionId, classId, student, date);
        int actualStudent = integer(profile.get("studentId"));
        int actualClass = integer(profile.get("classId"));
        if (actualClass == 0) actualClass = classId;
        String reportType = type == null || type.isBlank() ? "Default" : type;
        Map<String, Object> config = reportCardConfig(school, session, actualClass, term, reportType);

        List<String> examColumns = new ArrayList<>();
        double total = 0d;
        double maxTotal = 0d;
        for (Map<String, Object> row : subjects) {
            for (String key : new ArrayList<>(row.keySet())) {
                if (key.endsWith("_max") || SetOfReportCardFields.contains(key)) continue;
                if (key.equals("total") || key.equals("maxTotal") || key.equals("percentage")) continue;
                if (!key.equals("studentId") && !key.equals("registrationNo") && !key.equals("studentName")
                    && !key.equals("subjectId") && !key.equals("subject") && !key.equals("subjectCode")
                    && !key.equals("subjectOrder")) {
                    if (!examColumns.contains(key)) examColumns.add(key);
                }
            }
            total += decimal(row.get("total"), 0);
            maxTotal += decimal(row.get("maxTotal"), 0);
        }
        double overallPercentage = maxTotal == 0 ? 0 : Math.round(total * 10000d / maxTotal) / 100d;
        List<Map<String, Object>> grades = safe("""
            SELECT m_range_from AS rangeFrom,m_range_to AS rangeTo,grade,GP,color
            FROM chk_grade WHERE school_id=? ORDER BY m_range_from
            """, school);
        for (Map<String, Object> row : subjects) {
            double subjectMax = decimal(row.get("maxTotal"), 0);
            double subjectPercentage = subjectMax == 0 ? 0 : Math.round(decimal(row.get("total"), 0) * 10000d / subjectMax) / 100d;
            String grade = gradeFor(grades, subjectPercentage);
            row.put("Per", subjectPercentage);
            row.put("Grade", grade);
            row.put("subjectGrade", grade);
            row.put("totalTerm", row.get("total"));
            row.put("totalMaxTerm", row.get("maxTotal"));
            row.put("totalObtainedMarks", row.get("total"));
            row.put("overallGrade", gradeFor(grades, overallPercentage));
            row.put("overallPercentage", overallPercentage);
            row.put("Totalmax", maxTotal);
            row.put("TotalOverall", maxTotal == 0 ? 0 : 100d);
            for (String examColumn : examColumns) {
                row.putIfAbsent(examColumn, "");
                row.putIfAbsent(examColumn + "_max", "");
            }
        }

        out.put("student", profile);
        out.put("subjects", subjects);
        out.put("examColumns", examColumns);
        out.put("grades", grades);
        out.put("overall", Map.of("total", total, "maxTotal", maxTotal, "percentage", overallPercentage,
            "grade", gradeFor(grades, overallPercentage)));
        out.put("attendance", reportCardAttendance(school, session, actualStudent, actualClass));
        out.put("health", reportCardHealth(school, session, actualStudent));
        out.put("indicators", reportCardIndicators(school, session, actualStudent, actualClass));
        out.put("learnerProfile", reportCardLearnerProfile(school, session, actualStudent, term));
        out.put("categoryRemarks", reportCardCategoryRemarks(school, session, actualStudent));
        out.put("reportConfig", config);
        out.put("reportType", reportType);
        out.put("termId", term);
        out.put("date", date == null || date.isBlank() ? LocalDate.now().toString() : date);
        return out;
    }

    private Map<String, Object> reportCardStudent(String school, String session, int sectionId, int classId,
                                                   String student, String date) {
        List<Map<String, Object>> rows = safe("""
            SELECT TOP 1 sm.RollNo AS rollNo,sm.Registration_NO AS registrationNo,sm.uid,
              sm.stu_id AS studentId,sm.class_id AS classId,sm.sec_id AS sectionId,
              cn.class_name AS className,c.section AS sectionName,cn.class_name+'('+c.section+')' AS classSection,
              CASE WHEN TRY_CONVERT(date,o.DOB) IS NULL OR TRY_CONVERT(date,o.DOB)='1900-01-01' THEN ''
                   ELSE CONVERT(varchar(100),TRY_CONVERT(date,o.DOB),106) END AS dob,
              (SELECT TOP 1 h_name FROM HouseMaster h WHERE h.school_id=sm.school_id AND h.status=1 AND h.house_id=sm.House_id) AS house,
              UPPER(COALESCE(o.First_name,'')+' '+COALESCE(o.Mid_name,'')+' '+COALESCE(o.Last_name,'')) AS studentName,
              COALESCE(o.studentimage,'') AS studentImagePath,
              UPPER(COALESCE(o.Father_name,'')) AS fatherName,UPPER(COALESCE(o.Mother_name,'')) AS motherName,
              UPPER(COALESCE(o.R_Address,'')) AS address,o.Phone AS phone,o.Mobile AS mobile,
              o.student_aadhar AS studentAadhar,COALESCE(NULLIF(o.UDISE,''),s.UDISE) AS udise,
              s.School_name_Report AS schoolName,s.address AS schoolAddress,s.address_line1 AS schoolAddressLine1,
              s.affiliation_no AS affiliationNo,s.Pincode AS pincode,s.tc_add1 AS tcAdd1,s.tc_add2 AS tcAdd2,s.tc_add3 AS tcAdd3,
              s.Software_Domain AS webAddress,s.web_address AS schoolPortalUrl,s.logo_path AS schoolLogoPath,s.cbse_logo AS cbseLogoPath,
              s.logo_new AS schoolLogoNewPath,s.cambridge_logopath AS cambridgeLogoPath,s.watermark AS watermarkPath,
              s.[e-mail] AS schoolEmail,s.WebsiteUrl AS websiteUrl,s.mobile AS schoolMobile,
              st.State_Name AS stateName,d.City_Name AS cityName,cn.promoted_class AS promotedClass,
              c.ClassType AS classType
            FROM TB_StudentMaster sm
            LEFT JOIN student_other_info o ON o.school_id=sm.school_id AND o.Registration_NO=sm.Registration_NO
            JOIN TB_Class c ON c.school_id=sm.school_id AND c.sec_id=sm.sec_id
            JOIN class_name cn ON cn.school_id=c.school_id AND cn.id=c.class_id
            LEFT JOIN schoolmaster s ON s.school_id=sm.school_id
            LEFT JOIN State st ON st.State_Id=s.state_id
            LEFT JOIN Districts d ON d.state_id=s.state_id AND d.City_Id=s.city_id
            WHERE sm.school_id=CONVERT(varchar(50),?) AND sm.session=CONVERT(varchar(50),?) AND (?=0 OR sm.sec_id=?)
              AND (?=0 OR sm.class_id=?) AND (?='' OR CONVERT(varchar(50),sm.stu_id)=CONVERT(varchar(50),?))
            ORDER BY sm.stu_id
            """, school, session, sectionId, sectionId, classId, classId, student, student);
        Map<String, Object> out = rows.isEmpty() ? new LinkedHashMap<>() : new LinkedHashMap<>(rows.get(0));
        String web = text(out.get("webAddress"));
        out.put("schoolLogo", joinWebPath(web, text(out.get("schoolLogoPath"))));
        out.put("cbseLogo", joinWebPath(web, text(out.get("cbseLogoPath"))));
        out.put("schoolLogoNew", joinWebPath(web, text(out.get("schoolLogoNewPath"))));
        out.put("cambridgeLogo", joinWebPath(web, text(out.get("cambridgeLogoPath"))));
        out.put("watermark", joinWebPath(web, text(out.get("watermarkPath"))));
        out.put("studentImage", joinWebPath(web, text(out.get("studentImagePath")).replace("~/", "")));
        out.put("reportDate", date == null || date.isBlank() ? LocalDate.now().toString() : date);
        int actualClass = integer(out.get("classId"));
        int section = integer(out.get("sectionId"));
        out.putAll(reportCardTeacher(school, session, actualClass, section, web));
        out.putAll(reportCardSignatures(school, session, actualClass, web));
        Map<String, Object> attendance = reportCardAttendanceSummary(school, session, integer(out.get("studentId")));
        out.putAll(attendance);
        return out;
    }

    private Map<String, Object> reportCardTeacher(String school, String session, int classId, int sectionId, String web) {
        List<Map<String, Object>> rows = safe("""
            SELECT TOP 1 UPPER(COALESCE(f.First_name,'')+' '+COALESCE(f.Mid_name,'')+' '+COALESCE(f.Last_name,'')) AS teacherName,
              COALESCE(f.F_signature,'') AS teacherSignature
            FROM TB_FacultyMaster f JOIN Tb_ClassTeacher ct ON f.school_id=ct.school_id AND f.staff_u_id=ct.staff_u_id AND f.F_id=ct.F_id
            WHERE f.school_id=? AND ct.class_id=? AND ct.section_id=? AND ct.session=?
            ORDER BY ct.ct_id DESC
            """, school, classId, sectionId, session);
        Map<String, Object> out = new LinkedHashMap<>();
        if (!rows.isEmpty()) {
            out.put("classTeacherName", text(rows.get(0).get("teacherName")) + " </br>(CLASS TEACHER)");
            out.put("classTeacherSign", joinWebPath(web, text(rows.get(0).get("teacherSignature")).replace("~/", "")));
        } else {
            out.put("classTeacherName", ""); out.put("classTeacherSign", "");
        }
        return out;
    }

    private Map<String, Object> reportCardSignatures(String school, String session, int classId, String web) {
        List<Map<String, Object>> rows = safe("""
            SELECT d.Desg_id AS designationId,UPPER(COALESCE(f.First_name,'')+' '+COALESCE(f.Mid_name,'')+' '+COALESCE(f.Last_name,'')) AS facultyName,
              UPPER(COALESCE(g.DisplayName,'')) AS designationName,COALESCE(f.F_signature,'') AS signature
            FROM TB_FacultyMaster f JOIN desig_mapping d ON f.school_id=d.school_id AND f.staff_u_id=d.staff_u_id
            LEFT JOIN TB_Desig g ON g.school_id=d.school_id AND g.Desg_Id=d.Desg_id
            WHERE f.school_id=? AND f.status=1 AND d.Desg_id IN (37,70,71,72)
            ORDER BY d.Desg_id,f.id
            """, school);
        Map<String, Object> out = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            String id = text(row.get("designationId"));
            String name = text(row.get("facultyName"));
            String designation = text(row.get("designationName"));
            String displayName = name + (designation.isBlank() ? "" : "</br>(" + designation + ")");
            String sign = joinWebPath(web, text(row.get("signature")).replace("~/", ""));
            if ("37".equals(id)) { out.putIfAbsent("principal", displayName); out.putIfAbsent("principalSign", sign); }
            if ("70".equals(id)) out.putIfAbsent("vicePrincipal", name);
            if ("71".equals(id) || "72".equals(id)) { out.putIfAbsent("designName", displayName); out.putIfAbsent("designSign", sign); }
        }
        out.putIfAbsent("principal", ""); out.putIfAbsent("principalSign", ""); out.putIfAbsent("vicePrincipal", "");
        out.putIfAbsent("designName", ""); out.putIfAbsent("designSign", "");
        return out;
    }

    private Map<String, Object> reportCardAttendanceSummary(String school, String session, int studentId) {
        Map<String, Object> out = new LinkedHashMap<>();
        List<Map<String, Object>> rows = safe("""
            SELECT term,SUM(CASE WHEN ISNUMERIC(attendance)=1 THEN TRY_CONVERT(decimal(18,2),attendance) ELSE 0 END) AS attendance,
              SUM(CASE WHEN ISNUMERIC(work_Day)=1 THEN TRY_CONVERT(decimal(18,2),work_Day) ELSE 0 END) AS workingDays,
              CAST(CASE WHEN SUM(CASE WHEN ISNUMERIC(work_Day)=1 THEN TRY_CONVERT(decimal(18,2),work_Day) ELSE 0 END)=0 THEN 0
                ELSE SUM(CASE WHEN ISNUMERIC(attendance)=1 THEN TRY_CONVERT(decimal(18,2),attendance) ELSE 0 END)*100.0 /
                  SUM(CASE WHEN ISNUMERIC(work_Day)=1 THEN TRY_CONVERT(decimal(18,2),work_Day) ELSE 0 END) END AS decimal(18,2)) AS percentage
            FROM month_attendance WHERE school_id=CONVERT(varchar(50),?) AND session=CONVERT(varchar(50),?) AND stu_id=CONVERT(varchar(50),?) GROUP BY term ORDER BY term OPTION (RECOMPILE)
            """, school, session, studentId);
        out.put("attendanceSummary", rows);
        Map<String, Object> term1 = rows.stream().filter(x -> integer(x.get("term")) == 1).findFirst().orElse(Map.of());
        Map<String, Object> term2 = rows.stream().filter(x -> integer(x.get("term")) == 2).findFirst().orElse(Map.of());
        out.put("attendanceTerm1", term1); out.put("attendanceTerm2", term2);
        return out;
    }

    private List<Map<String, Object>> reportCardAttendance(String school, String session, int studentId, int classId) {
        List<Map<String, Object>> rows = safe("""
            SELECT term,SUM(CASE WHEN ISNUMERIC(attendance)=1 THEN TRY_CONVERT(decimal(18,2),attendance) ELSE 0 END) AS attendance,
              SUM(CASE WHEN ISNUMERIC(work_Day)=1 THEN TRY_CONVERT(decimal(18,2),work_Day) ELSE 0 END) AS workingDays,
              CAST(CASE WHEN SUM(CASE WHEN ISNUMERIC(work_Day)=1 THEN TRY_CONVERT(decimal(18,2),work_Day) ELSE 0 END)=0 THEN 0
                ELSE SUM(CASE WHEN ISNUMERIC(attendance)=1 THEN TRY_CONVERT(decimal(18,2),attendance) ELSE 0 END)*100.0 /
                  SUM(CASE WHEN ISNUMERIC(work_Day)=1 THEN TRY_CONVERT(decimal(18,2),work_Day) ELSE 0 END) END AS decimal(18,2)) AS percentage
            FROM month_attendance WHERE school_id=CONVERT(varchar(50),?) AND session=CONVERT(varchar(50),?) AND stu_id=CONVERT(varchar(50),?) GROUP BY term ORDER BY term OPTION (RECOMPILE)
            """, school, session, studentId);
        return rows;
    }

    private List<Map<String, Object>> reportCardHealth(String school, String session, int studentId) {
        return safe("""
            SELECT h.term_id AS termId,h.Height AS height,h.Weight AS weight,h.teeth,h.Blood_Gp AS bloodGp,
              bg.blood_group AS bloodGroup,h.Vision AS vision,h.Oral_Hygiene AS oralHygiene,h.Specificaliment AS specificAilment
            FROM HealthStatus h LEFT JOIN blood_group bg ON bg.id=TRY_CONVERT(int,h.Blood_Gp)
            WHERE h.school_id=CONVERT(varchar(50),?) AND h.session=CONVERT(varchar(50),?) AND h.Stu_ID=? ORDER BY h.term_id OPTION (RECOMPILE)
            """, school, session, studentId);
    }

    private List<Map<String, Object>> reportCardIndicators(String school, String session, int studentId, int classId) {
        return safe("""
            SELECT i.IndicatorOrder AS indicatorOrder,i.indicator_cat AS indicatorCategory,i.indicator_Name AS indicatorName,
              i.indicator_ID AS indicatorId,
              (SELECT TOP 1 g.Grade FROM tb_indicator_Grade g WHERE g.school_id=i.school_id AND g.session=i.session AND g.stu_id=CONVERT(varchar(50),?) AND g.indicator_ID=i.indicator_ID AND g.Term_id='1') AS term1,
              (SELECT TOP 1 g.Grade FROM tb_indicator_Grade g WHERE g.school_id=i.school_id AND g.session=i.session AND g.stu_id=CONVERT(varchar(50),?) AND g.indicator_ID=i.indicator_ID AND g.Term_id='2') AS term2
            FROM tb_indicator i WHERE i.school_id=CONVERT(varchar(50),?) AND i.session=CONVERT(varchar(50),?) AND i.class_id=? AND i.status='active'
            ORDER BY i.IndicatorOrder OPTION (RECOMPILE)
            """, studentId, studentId, school, session, classId);
    }

    private List<Map<String, Object>> reportCardLearnerProfile(String school, String session, int studentId, String term) {
        return safe("""
            SELECT a.ATTRIBUTE_ID AS attributeId,a.ATTRIBUTE_NAME AS attributeName,
              COALESCE(r.ATTRIBUTE_REMARK,'') AS attributeRemark,r.Attribute_Value_Id AS attributeValueIds,
              COALESCE(r.TERM_ID,0) AS termId
            FROM TB_LERNERPROFILE_ATTRIBUTE a
            LEFT JOIN TB_LERNERPROFILE_ATTRIBUTE_REMARKS r ON r.SCHOOL_ID=a.SCHOOL_ID AND r.SESSION=a.SESSION
              AND r.ATTRIBUTE_ID=a.ATTRIBUTE_ID AND r.STU_ID=? AND (?='' OR r.TERM_ID=? OR r.TERM_ID IS NULL)
            WHERE a.STATUS='active' AND a.SCHOOL_ID=? AND a.SESSION=? ORDER BY a.ATTR_ORDER
            """, studentId, term, term, school, session);
    }

    private List<Map<String, Object>> reportCardCategoryRemarks(String school, String session, int studentId) {
        return safe("""
            SELECT c.CategoryName AS categoryName,s.CategoryName AS subCategoryName,
              COALESCE(MAX(CASE WHEN r.term_id='1' THEN r.Remark ELSE '-' END),'-') AS term1,
              COALESCE(MAX(CASE WHEN r.term_id='2' THEN r.Remark ELSE '-' END),'-') AS term2,
              c.CategoryOrder AS categoryOrder,s.SubCategoryOrder AS subCategoryOrder
            FROM ReportCardCategory c JOIN ReportCardSubCategory s ON c.school_id=s.school_id AND s.session=c.session AND c.id=s.CategoryId
            LEFT JOIN ReportCardSubCategoryRemark r ON c.school_id=r.school_id AND s.session=r.session
              AND s.CategoryId=r.CategoryId AND s.id=r.SubCategoryId AND r.stu_id=CONVERT(varchar(50),?)
            WHERE c.school_id=CONVERT(varchar(50),?) AND c.session=CONVERT(varchar(50),?) AND c.status=1 AND s.status=1
            GROUP BY c.CategoryName,s.CategoryName,c.CategoryOrder,s.SubCategoryOrder
            ORDER BY c.CategoryOrder,s.SubCategoryOrder OPTION (RECOMPILE)
            """, studentId, school, session);
    }

    private Map<String, Object> reportCardConfig(String school, String session, int classId, String term, String type) {
        List<Map<String, Object>> rows = safe("""
            SELECT TOP 1 rdlc_report AS rdlcReport,Type AS reportType,ReportCardType AS reportCardType,
              class_id AS classId,term_id AS termId,classtype,ReportType AS reportTypeName
            FROM reportcard_rdlc WHERE school_id=? AND session=? AND class_id=? AND ReportCardType='TermWise'
              AND (?='' OR term_id=TRY_CONVERT(int,?)) AND (Type=? OR ?='') ORDER BY id
            """, school, session, classId, term, term, type, type);
        return rows.isEmpty() ? Map.of("rdlcReport", "", "reportType", type) : rows.get(0);
    }

    private static String gradeFor(List<Map<String, Object>> grades, double percentage) {
        for (Map<String, Object> grade : grades) {
            if (percentage >= decimal(grade.get("rangeFrom"), Double.NEGATIVE_INFINITY)
                && percentage <= decimal(grade.get("rangeTo"), Double.POSITIVE_INFINITY)) return text(grade.get("grade"));
        }
        return "";
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> listValue(Object value) {
        return value instanceof List<?> list ? (List<Map<String, Object>>) list : new ArrayList<>();
    }

    private static String joinWebPath(String web, String path) {
        if (path == null || path.isBlank()) return "";
        if (path.startsWith("http://") || path.startsWith("https://") || path.startsWith("data:")) return path;
        if (web == null || web.isBlank()) return path;
        return web.replaceAll("/+$", "") + "/" + path.replaceAll("^/+", "");
    }

    private static final java.util.Set<String> SetOfReportCardFields = java.util.Set.of(
        "studentId", "registrationNo", "studentName", "subjectId", "subject", "subjectCode", "subjectOrder",
        "total", "maxTotal", "percentage", "Subject", "Per", "Grade", "subjectGrade", "totalTerm",
        "totalMaxTerm", "totalObtainedMarks", "overallGrade", "overallPercentage", "Total", "Totalmax", "TotalOverall");

    /** One chart data row per subject/exam, matching get_marks_for_graph*. */
    private List<Map<String, Object>> academicRows(String school, String session, int sectionId, int classId,
                                                    String student) {
        return safe("""
            SELECT s.exam_type_id AS examTypeId,et.exam_type AS examType,
              s.subject_id AS subjectId,sub.Subject AS subjectName,
              s.marks,s.max_marks AS maxMarks,cs.subject_order AS subjectOrder
            FROM senior_marks s
            JOIN TB_StudentMaster sm ON sm.school_id=s.school_id AND sm.session=s.session AND sm.stu_id=s.stu_id
            LEFT JOIN exam_type et ON et.school_id=s.school_id AND et.exam_type_id=TRY_CONVERT(int,s.exam_type_id)
            LEFT JOIN TB_SubjectMaster sub ON sub.school_id=s.school_id AND sub.id=s.subject_id
            LEFT JOIN TB_ClassSubject cs ON cs.school_id=s.school_id AND cs.session=s.session
              AND cs.class_id=CASE WHEN ? IN (17,18) THEN ? ELSE ? END AND cs.subject_id=s.subject_id AND cs.status=1
            WHERE s.school_id=? AND s.session=? AND LOWER(sm.status)='registered'
              AND (?=0 OR s.sec_id=?) AND (?='' OR CONVERT(varchar(50),s.stu_id)=?)
              AND EXISTS (SELECT 1 FROM tb_exam_max_min_new mm WHERE mm.school_id=s.school_id AND mm.Session=s.session
                AND mm.subject_id=s.subject_id AND mm.Grade_subject='N'
                AND mm.class_id=CASE WHEN ? IN (17,18) THEN ? ELSE ? END)
            ORDER BY s.exam_type_id,cs.subject_order,subjectName
            """, classId, sectionId, classId, school, session, sectionId, sectionId, student, student,
            classId, sectionId, classId);
    }

    private List<Map<String, Object>> cbseRows(String school, String session, int sectionId, int requestedClassId,
                                                Map<String, Object> request) {
        int classId = cbseClassId(school, requestedClassId);
        String type = text(request.get("type"));
        String stream = text(request.get("stream"));
        if ("4".equals(type) || "cbse-main".equalsIgnoreCase(text(request.get("report")))) {
            return cbseSubjectRows(school, session, sectionId, classId, stream);
        }
        if ("7".equals(type)) return cbseGlanceRows(school, session, classId, stream);
        List<Map<String, Object>> rows = cbseStudentRows(school, session, sectionId, classId, stream);
        if ("3".equals(type)) {
            rows = new ArrayList<>(rows.stream().limit(5).toList());
        }
        return rows;
    }

    private List<Map<String, Object>> cbseStudentRows(String school, String session, int sectionId, int classId,
                                                       String stream) {
        return safe("""
            WITH aa AS (
              SELECT sm.stu_id,sm.registration_no,sm.class_id,sm.sec_id,c.section,
                UPPER(REPLACE(COALESCE(o.first_name,'')+' '+COALESCE(o.mid_name,'')+' '+COALESCE(o.last_name,''),'  ',' ')) AS student_name,
                COALESCE(NULLIF(o.stream,''),'') AS stream,
                SUM(CASE WHEN ISNUMERIC(s.marks)=1 THEN TRY_CONVERT(decimal(18,2),s.marks) ELSE 0 END) AS total_marks,
                SUM(CASE WHEN ISNUMERIC(s.max_marks)=1 THEN TRY_CONVERT(decimal(18,2),s.max_marks) ELSE 0 END) AS max_marks
              FROM TB_StudentMaster sm
              JOIN senior_marks s ON s.school_id=sm.school_id AND s.session=sm.session AND s.stu_id=sm.stu_id
              LEFT JOIN student_other_info o ON o.school_id=sm.school_id AND o.registration_no=sm.registration_no
              LEFT JOIN TB_Class c ON c.school_id=sm.school_id AND c.sec_id=sm.sec_id
              WHERE sm.school_id=? AND sm.session=? AND LOWER(sm.status)='registered' AND sm.class_id=?
                AND (?=0 OR sm.sec_id=?) AND (?='' OR COALESCE(NULLIF(o.stream,''),'')=?)
              GROUP BY sm.stu_id,sm.registration_no,sm.class_id,sm.sec_id,c.section,o.first_name,o.mid_name,o.last_name,o.stream
            )
            SELECT stu_id,registration_no,student_name,stream,class_id,sec_id,section,total_marks,max_marks,
              CAST(CASE WHEN max_marks=0 THEN 0 ELSE total_marks*100.0/max_marks END AS decimal(18,2)) AS percentage,
              DENSE_RANK() OVER(ORDER BY CASE WHEN max_marks=0 THEN 0 ELSE total_marks*100.0/max_marks END DESC) AS rank
            FROM aa ORDER BY percentage DESC,student_name
            """, school, session, classId, sectionId, sectionId, stream, stream);
    }

    private List<Map<String, Object>> cbseSubjectRows(String school, String session, int sectionId, int classId,
                                                       String stream) {
        return safe("""
            SELECT s.subject_id,sub.Subject AS subject_name,
              COUNT(DISTINCT s.stu_id) AS student_count,
              CAST(AVG(CASE WHEN ISNUMERIC(s.marks)=1 AND TRY_CONVERT(decimal(18,2),s.max_marks)>0
                THEN TRY_CONVERT(decimal(18,2),s.marks)*100.0/TRY_CONVERT(decimal(18,2),s.max_marks) ELSE 0 END) AS decimal(18,2)) AS average_percentage,
              MAX(CASE WHEN ISNUMERIC(s.marks)=1 THEN TRY_CONVERT(decimal(18,2),s.marks) ELSE 0 END) AS highest_marks,
              MAX(s.max_marks) AS max_marks
            FROM senior_marks s
            JOIN TB_StudentMaster sm ON sm.school_id=s.school_id AND sm.session=s.session AND sm.stu_id=s.stu_id
            LEFT JOIN student_other_info o ON o.school_id=sm.school_id AND o.registration_no=sm.registration_no
            LEFT JOIN TB_SubjectMaster sub ON sub.school_id=s.school_id AND sub.id=s.subject_id
            WHERE s.school_id=? AND s.session=? AND LOWER(sm.status)='registered' AND sm.class_id=?
              AND (?=0 OR sm.sec_id=?) AND (?='' OR COALESCE(NULLIF(o.stream,''),'')=?)
            GROUP BY s.subject_id,sub.Subject ORDER BY sub.Subject
            """, school, session, classId, sectionId, sectionId, stream, stream);
    }

    private List<Map<String, Object>> cbseGlanceRows(String school, String session, int classId, String stream) {
        return safe("""
            SELECT sm.sec_id,c.section,COUNT(DISTINCT sm.stu_id) AS student_count,
              CAST(AVG(CASE WHEN TRY_CONVERT(decimal(18,2),s.max_marks)>0
                THEN TRY_CONVERT(decimal(18,2),s.marks)*100.0/TRY_CONVERT(decimal(18,2),s.max_marks) ELSE 0 END) AS decimal(18,2)) AS average_percentage,
              SUM(CASE WHEN TRY_CONVERT(decimal(18,2),s.max_marks)>0 AND TRY_CONVERT(decimal(18,2),s.marks)*100.0/TRY_CONVERT(decimal(18,2),s.max_marks)>=33 THEN 1 ELSE 0 END) AS pass_entries
            FROM TB_StudentMaster sm
            JOIN senior_marks s ON s.school_id=sm.school_id AND s.session=sm.session AND s.stu_id=sm.stu_id
            LEFT JOIN student_other_info o ON o.school_id=sm.school_id AND o.registration_no=sm.registration_no
            LEFT JOIN TB_Class c ON c.school_id=sm.school_id AND c.sec_id=sm.sec_id
            WHERE sm.school_id=? AND sm.session=? AND LOWER(sm.status)='registered' AND sm.class_id=?
              AND (?='' OR COALESCE(NULLIF(o.stream,''),'')=?)
            GROUP BY sm.sec_id,c.section ORDER BY sm.sec_id
            """, school, session, classId, stream, stream);
    }

    private int cbseClassId(String school, int requestedClassId) {
        int mapped = requestedClassId == 10 ? 16 : requestedClassId == 12 ? 18 : requestedClassId;
        return scalarInt("SELECT TOP 1 id FROM class_name WHERE school_id=? AND id=?", school, mapped);
    }

    /** ExamComparativeSheet honours its multi-select exam list and final-calculation exam. */
    private List<Map<String, Object>> comparativeRows(String school, String session, int sectionId, int classId,
                                                       Map<String, Object> request) {
        List<String> exams = textList(request.get("exams"));
        String examClause = exams.isEmpty() ? "" : " AND s.exam_type_id IN (" + "?,".repeat(exams.size()).replaceAll(",$", "") + ")";
        List<Object> args = new ArrayList<>(List.of(school, session, sectionId, sectionId, classId, classId));
        args.addAll(exams);
        String sql = """
            SELECT s.stu_id AS studentId,sm.registration_no AS registrationNo,
              UPPER(REPLACE(COALESCE(o.first_name,'')+' '+COALESCE(o.mid_name,'')+' '+COALESCE(o.last_name,''),'  ',' ')) AS studentName,
              s.exam_type_id AS examTypeId,et.exam_type AS examType,
              SUM(COALESCE(TRY_CONVERT(decimal(18,2),s.marks),0)) AS total,
              SUM(COALESCE(TRY_CONVERT(decimal(18,2),s.max_marks),0)) AS maxTotal
            FROM senior_marks s
            JOIN TB_StudentMaster sm ON sm.school_id=s.school_id AND sm.session=s.session AND sm.stu_id=s.stu_id
            LEFT JOIN student_other_info o ON o.school_id=sm.school_id AND o.registration_no=sm.registration_no
            LEFT JOIN exam_type et ON et.school_id=s.school_id AND et.exam_type_id=TRY_CONVERT(int,s.exam_type_id)
            WHERE s.school_id=? AND s.session=? AND LOWER(sm.status)='registered'
              AND (?=0 OR s.sec_id=?) AND (?=0 OR sm.class_id=?)
            """ + examClause + """
            GROUP BY s.stu_id,sm.registration_no,o.first_name,o.mid_name,o.last_name,s.exam_type_id,et.exam_type
            ORDER BY studentName,s.exam_type_id
            """;
        List<Map<String, Object>> rows = safe(sql, args.toArray());
        for (Map<String, Object> row : rows) {
            double max = decimal(row.get("maxTotal"), 0);
            row.put("percentage", max == 0 ? 0 : Math.round(decimal(row.get("total"), 0) * 10000 / max) / 100d);
        }
        return rows;
    }

    private List<Map<String, Object>> markStatusRows(String school, String session, int sectionId, int classId, String exam) {
        return safe("""
            WITH marks AS (
              SELECT s.exam_type_id,sm.class_id,s.subject_id,sm.sec_id,
                CONVERT(varchar(100),MAX(s.modified_date),100) AS lastupdatedate,
                CAST(COUNT(s.stu_id) AS varchar(50))+'/'+CAST(COUNT(sm.stu_id) AS varchar(50)) AS studentcount,
                CASE WHEN COUNT(s.stu_id)>0 THEN '1' ELSE '0' END AS marksstatus
              FROM senior_marks s
              JOIN TB_StudentMaster sm ON sm.school_id=s.school_id AND sm.session=s.session AND sm.stu_id=s.stu_id
              WHERE s.school_id=? AND s.session=? AND LOWER(sm.status)='registered'
                AND (?=0 OR sm.class_id=?) AND (?=0 OR sm.sec_id=?)
                AND (?='' OR CONVERT(varchar(50),s.exam_type_id)=?)
              GROUP BY s.exam_type_id,sm.class_id,s.subject_id,sm.sec_id
            )
            SELECT ROW_NUMBER() OVER(ORDER BY tc.class_id,tc.sec_id,cs.subject_id,mm.exam_type_id) AS sno,
              CASE WHEN mm.exam_type_id IS NULL THEN '' ELSE 'examwise' END AS reporttype,
              tc.class_id,tc.sec_id,tc.section,mm.min_marks,mm.max_marks,mm.Grade_subject,cs.subject_id,
              mm.exam_type_id,et.exam_type,m.lastupdatedate,mm.E_Date,m.studentcount,m.marksstatus,
              cn.class_name,sub.Subject
            FROM TB_Class tc
            JOIN TB_ClassSubject cs ON cs.school_id=tc.school_id AND (cs.class_id=tc.class_id OR cs.class_id=tc.sec_id) AND cs.session=? AND cs.status=1
            LEFT JOIN tb_exam_max_min_new mm ON mm.school_id=tc.school_id AND mm.Session=?
              AND (mm.class_id=tc.class_id OR mm.class_id=tc.sec_id) AND mm.subject_id=cs.subject_id
              AND (?='' OR CONVERT(varchar(50),mm.exam_type_id)=?)
            LEFT JOIN exam_type et ON et.school_id=mm.school_id AND et.exam_type_id=mm.exam_type_id
            LEFT JOIN marks m ON m.exam_type_id=mm.exam_type_id AND m.class_id=tc.class_id AND m.sec_id=tc.sec_id AND m.subject_id=cs.subject_id
            LEFT JOIN class_name cn ON cn.school_id=tc.school_id AND cn.id=tc.class_id
            LEFT JOIN TB_SubjectMaster sub ON sub.school_id=tc.school_id AND sub.id=cs.subject_id
            WHERE tc.school_id=? AND (?=0 OR tc.class_id=?) AND (?=0 OR tc.sec_id=?)
            ORDER BY tc.class_id,tc.sec_id,cs.subject_id,mm.exam_type_id
            """, school, session, classId, classId, sectionId, sectionId, exam, exam, session, session, exam, exam,
            school, classId, classId, sectionId, sectionId);
    }

    private List<Map<String, Object>> shape(String report, List<Map<String, Object>> base, Map<String, Object> request) {
        String r = report.toLowerCase();
        if (r.contains("percentage")) {
            double from = decimal(request.get("fromPercent"), 0), to = decimal(request.get("toPercent"), 100);
            return base.stream().filter(x -> { double p = percentage(x); return p >= from && p <= to; }).toList();
        }
        if (r.contains("failure")) return base.stream().filter(x -> { double m=decimal(x.get("marks"),-1), min=decimal(x.get("minMarks"),0); return m>=0 && m<min; }).toList();
        if (r.contains("status") || r.contains("graph")) return statusRows(base);
        if (r.contains("topper")) return topperRows(base, integer(request.get("topN")));
        if (r.contains("overall") || r.contains("cumulative") || r.contains("academic") || r.contains("comparative") || r.contains("reportcard") || r.equals("reportcard")) return aggregateRows(base);
        if (r.contains("subject")) return subjectRows(base);
        return base;
    }

    private List<Map<String, Object>> aggregateRows(List<Map<String, Object>> base) {
        Map<String, Map<String,Object>> grouped = new LinkedHashMap<>();
        for (Map<String,Object> row : base) {
            String key=text(row.get("studentId")); Map<String,Object> out=grouped.computeIfAbsent(key,k->{Map<String,Object> n=new LinkedHashMap<>();n.put("studentId",row.get("studentId"));n.put("registrationNo",row.get("registrationNo"));n.put("studentName",row.get("studentName"));n.put("className",row.get("className"));n.put("sectionName",row.get("sectionName"));n.put("total",0d);n.put("maxTotal",0d);n.put("subjects",0);return n;});
            double marks=decimal(row.get("marks"),0), max=decimal(row.get("maxMarks"),0); out.put("total",decimal(out.get("total"),0)+marks); out.put("maxTotal",decimal(out.get("maxTotal"),0)+max); out.put("subjects",integer(out.get("subjects"))+1);
        }
        for(Map<String,Object> row:grouped.values()){double max=decimal(row.get("maxTotal"),0);row.put("percentage",max==0?0:Math.round(decimal(row.get("total"),0)*10000/max)/100d);}
        return new ArrayList<>(grouped.values());
    }

    private List<Map<String,Object>> topperRows(List<Map<String,Object>> base,int topN){List<Map<String,Object>> rows=aggregateRows(base);rows.sort((a,b)->Double.compare(decimal(b.get("percentage"),0),decimal(a.get("percentage"),0)));int n=topN>0?topN:rows.size();for(int i=0;i<rows.size();i++)rows.get(i).put("rank",i+1);return rows.subList(0,Math.min(n,rows.size()));}
    private List<Map<String,Object>> subjectRows(List<Map<String,Object>> base){Map<String,Map<String,Object>> m=new LinkedHashMap<>();for(Map<String,Object> r:base){String k=text(r.get("subjectId"));if(k.isBlank())continue;Map<String,Object> x=m.computeIfAbsent(k,z->new LinkedHashMap<>());x.put("subjectId",r.get("subjectId"));x.put("subjectName",r.get("subjectName"));x.put("className",r.get("className"));x.put("sectionName",r.get("sectionName"));x.put("subjectCode",r.get("subjectId"));x.put("students",integer(x.get("students"))+1);x.put("marksEntered",integer(x.get("marksEntered"))+(text(r.get("marks")).isBlank()?0:1));}return new ArrayList<>(m.values());}
    private List<Map<String,Object>> statusRows(List<Map<String,Object>> base){Map<String,Map<String,Object>> m=new LinkedHashMap<>();for(Map<String,Object> r:base){String k=text(r.get("subjectId"));Map<String,Object>x=m.computeIfAbsent(k,z->{Map<String,Object>n=new LinkedHashMap<>();n.put("subjectId",r.get("subjectId"));n.put("subjectName",r.get("subjectName"));n.put("total",0);n.put("entered",0);n.put("absent",0);n.put("pending",0);return n;});x.put("total",integer(x.get("total"))+1);if(!text(r.get("marks")).isBlank())x.put("entered",integer(x.get("entered"))+1);if(!text(r.get("absentism")).isBlank())x.put("absent",integer(x.get("absent"))+1);if(text(r.get("marks")).isBlank()&&text(r.get("absentism")).isBlank())x.put("pending",integer(x.get("pending"))+1);}return new ArrayList<>(m.values());}

    private List<String> columns(String report,List<Map<String,Object>> rows){if(!rows.isEmpty())return new ArrayList<>(rows.get(0).keySet());return List.of("studentName","subjectName","marks","maxMarks","percentage");}
    private double percentage(Map<String,Object> r){double m=decimal(r.get("marks"),0),max=decimal(r.get("maxMarks"),0);return max==0?0:m*100/max;}
    private int scalarInt(String sql,Object...args){try{Integer value=jdbc.queryForObject(sql,Integer.class,args);return value==null?0:value;}catch(Exception e){return 0;}}
    private String scalarText(String sql,Object...args){try{String value=jdbc.queryForObject(sql,String.class,args);return value==null?"":value.trim();}catch(Exception e){return "";}}
    private List<Map<String,Object>> safe(String sql,Object...args){try{return jdbc.queryForList(sql,args);}catch(DataAccessException e){return List.of();}}
    private static String text(Object v){return v==null?"":String.valueOf(v).trim();}
    private static int integer(Object v){try{return v==null?0:Integer.parseInt(String.valueOf(v).replaceAll("\\.0$",""));}catch(Exception e){return 0;}}
    private static List<Integer> integerList(Object value) {
        if (!(value instanceof List<?> values)) return List.of();
        List<Integer> out = new ArrayList<>();
        for (Object item : values) {
            int parsed = integer(item);
            if (parsed > 0 && !out.contains(parsed)) out.add(parsed);
        }
        return out;
    }
    private static List<String> textList(Object value) {
        if (!(value instanceof List<?> values)) return List.of();
        List<String> out = new ArrayList<>();
        for (Object item : values) {
            String parsed = text(item);
            if (!parsed.isBlank() && !out.contains(parsed)) out.add(parsed);
        }
        return out;
    }
    private static double decimal(Object v,double fallback){try{return v==null||String.valueOf(v).isBlank()?fallback:Double.parseDouble(String.valueOf(v));}catch(Exception e){return fallback;}}
}
