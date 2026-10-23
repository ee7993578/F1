package com.edufox.schoolerp.uploads;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class UploadsRepository {
    private final JdbcTemplate jdbc;

    public List<Map<String, Object>> sessions(String school) {
        return jdbc.queryForList("SELECT session AS value, session AS label, status FROM tb_session WHERE school_id=? ORDER BY CASE WHEN LOWER(status)='default' THEN 0 ELSE 1 END, session DESC", school);
    }

    public List<Map<String, Object>> classes(String school, String session, String staff) {
        String sql = "SELECT DISTINCT c.sec_id AS id,c.sec_id AS secId,c.class_id AS classId,cn.class_name+' ('+c.section+')' AS label,cn.class_name AS className,c.section " +
                "FROM tb_class c JOIN class_name cn ON cn.id=c.class_id AND cn.school_id=c.school_id " +
                "JOIN TB_StudentMaster st ON st.school_id=c.school_id AND st.session=? AND st.sec_id=c.sec_id AND LOWER(st.status)='registered' " +
                "WHERE c.school_id=? ORDER BY c.class_id,c.section,c.sec_id";
        return jdbc.queryForList(sql, session, school);
    }

    public List<Map<String, Object>> classesForSyllabus(String school, String session, String staff) {
        return classes(school, session, staff);
    }

    public List<Map<String, Object>> sections(String school, String session, String classId) {
        if (blank(classId) || "0".equals(classId)) return List.of();
        return jdbc.queryForList("SELECT c.sec_id AS id,c.sec_id AS secId,c.class_id AS classId,cn.class_name+' ('+c.section+')' AS label,c.section,cn.class_name AS className FROM tb_class c JOIN class_name cn ON cn.id=c.class_id AND cn.school_id=c.school_id WHERE c.school_id=? AND c.class_id=? ORDER BY c.section,c.sec_id", school, classId);
    }

    public List<Map<String, Object>> subjects(String school, String session, String secId, String classId) {
        return jdbc.queryForList("SELECT cs.subject_id AS id,cs.subject_id AS subjectId,COALESCE(NULLIF(LTRIM(RTRIM(sm.Subject)),''),cs.subject_code) AS name,cs.subject_code AS code,cs.subject_order AS subjectOrder FROM TB_ClassSubject cs LEFT JOIN TB_SubjectMaster sm ON sm.id=cs.subject_id AND sm.school_id=cs.school_id WHERE cs.school_id=? AND cs.session=? AND cs.class_id=? AND ISNULL(cs.status,1)=1 GROUP BY cs.subject_id,sm.Subject,cs.subject_code,cs.subject_order ORDER BY ISNULL(cs.subject_order,0),name", school, session, classId);
    }

    public List<Map<String, Object>> students(String school, String session, String secId) {
        return jdbc.queryForList("SELECT st.stu_id AS id,st.stu_id AS studentId,st.registration_no AS registrationNo,UPPER(LTRIM(RTRIM(COALESCE(so.First_name,'')+' '+COALESCE(so.Mid_name,'')+' '+COALESCE(so.Last_name,'')))) AS name FROM TB_StudentMaster st LEFT JOIN student_other_info so ON so.school_id=st.school_id AND so.Registration_NO=st.Registration_NO WHERE st.school_id=? AND st.session=? AND st.sec_id=? AND LOWER(st.status)='registered' ORDER BY name,st.registration_no", school, session, secId);
    }

    public List<Map<String, Object>> terms(String school, String session, String classId) {
        return jdbc.queryForList("SELECT DISTINCT es.term_id AS id,es.term_id AS termId,COALESCE(NULLIF(LTRIM(RTRIM(t.term_name)),''),CONVERT(varchar(20),es.term_id)) AS name FROM exam_set es LEFT JOIN term t ON t.term_id=es.term_id WHERE es.school_id=? AND es.term_id IS NOT NULL AND EXISTS (SELECT 1 FROM assign_exam_set a WHERE a.school_id=es.school_id AND a.exam_set_id=es.exam_set_id AND a.class_id=? AND a.from_year<=TRY_CONVERT(int,LEFT(?,4)) AND (a.to_year=0 OR a.to_year>=TRY_CONVERT(int,RIGHT(?,4)))) ORDER BY es.term_id", school, classId, session, session);
    }

    public List<Map<String, Object>> exams(String school, String session, String classId, String termId) {
        return jdbc.queryForList("SELECT es.exam_type_id AS id,es.exam_type_id AS examTypeId,COALESCE(NULLIF(LTRIM(RTRIM(et.exam_type)),''),es.exam_name_dis) AS name,es.exam_order AS examOrder FROM exam_set es LEFT JOIN exam_type et ON et.exam_type_id=es.exam_type_id AND et.school_id=es.school_id WHERE es.school_id=? AND es.term_id=? AND EXISTS (SELECT 1 FROM assign_exam_set a WHERE a.school_id=es.school_id AND a.exam_set_id=es.exam_set_id AND a.class_id=? AND a.from_year<=TRY_CONVERT(int,LEFT(?,4)) AND (a.to_year=0 OR a.to_year>=TRY_CONVERT(int,RIGHT(?,4)))) GROUP BY es.exam_type_id,et.exam_type,es.exam_name_dis,es.exam_order ORDER BY es.exam_order,es.exam_type_id", school, termId, classId, session, session);
    }

    public List<Map<String, Object>> faculties(String school) {
        return jdbc.queryForList("SELECT f_id AS id,f_id AS facultyId,UPPER(LTRIM(RTRIM(COALESCE(first_name,'')+' '+COALESCE(mid_name,'')+' '+COALESCE(last_name,'')))) AS name FROM TB_FacultyMaster WHERE school_id=? AND LOWER(ISNULL(status,'')) IN ('active','1','') ORDER BY name", school);
    }

    public List<Map<String, Object>> planners(String school, String session) {
        return jdbc.queryForList("SELECT id,title,file_path AS filePath,session FROM academic_planner WHERE school_id=? AND session=? ORDER BY id DESC", school, session);
    }

    public List<Map<String, Object>> syllabi(String school, String session) {
        return jdbc.queryForList("SELECT u.syllabus_id AS id,u.syllabus_id AS syllabusId,u.title,u.file_path AS filePath,u.class_id AS classId,u.sec_id AS secId,u.term_id AS termId,u.exam_type_id AS examTypeId,COALESCE(cn.class_name,CONVERT(varchar(20),u.class_id)) AS className,COALESCE(c.section,'') AS section,COALESCE(et.exam_type,CONVERT(varchar(20),u.exam_type_id)) AS examType FROM upload_syllabus u LEFT JOIN class_name cn ON cn.id=TRY_CONVERT(int,u.class_id) AND cn.school_id=u.school_id LEFT JOIN tb_class c ON c.sec_id=TRY_CONVERT(int,u.sec_id) AND c.school_id=u.school_id LEFT JOIN exam_type et ON et.exam_type_id=TRY_CONVERT(int,u.exam_type_id) AND et.school_id=u.school_id WHERE u.school_id=? AND u.session=? AND ISNULL(u.status,1)=1 ORDER BY u.syllabus_id DESC", school, session);
    }

    public List<Map<String, Object>> holidayHomework(String school, String session) {
        return jdbc.queryForList("SELECT h.id,h.title_id AS titleId,h.title,h.class_id AS classId,h.sec_id AS secId,h.file_path AS filePath,h.submission_date AS submissionDate,h.f_id AS facultyId,COALESCE(cn.class_name,CONVERT(varchar(20),h.class_id))+' ('+COALESCE(c.section,'')+')' AS className,COALESCE(hl.link_title,'') AS linkTitle FROM holiday_homework h LEFT JOIN class_name cn ON cn.id=h.class_id AND cn.school_id=h.school_id LEFT JOIN tb_class c ON c.sec_id=h.sec_id AND c.school_id=h.school_id LEFT JOIN holiday_link hl ON hl.school_id=h.school_id AND hl.session=h.session AND hl.status=1 WHERE h.school_id=? AND h.session=? AND ISNULL(h.status,1)=1 ORDER BY h.id DESC", school, session);
    }

    public String holidayLink(String school, String session) {
        List<Map<String,Object>> rows = jdbc.queryForList("SELECT TOP 1 link_title FROM holiday_link WHERE school_id=? AND session=? AND status=1 ORDER BY id DESC", school, session);
        return rows.isEmpty() ? "" : Objects.toString(rows.get(0).get("link_title"), "");
    }

    public List<Map<String, Object>> calendar(String school, String session) {
        return jdbc.queryForList("SELECT id,event_date AS eventDate,event_to_date AS eventToDate,event_name AS eventName FROM school_calender WHERE school_id=? AND session=? AND status=1 ORDER BY TRY_CONVERT(date,event_date),id", school, session);
    }

    public List<Map<String, Object>> activities(String school, String session) {
        return jdbc.queryForList("SELECT activity_id AS id,activity_date AS activityDate,activity_name AS activityName FROM Activity WHERE school_id=? AND session=? ORDER BY TRY_CONVERT(date,activity_date),activity_id", school, session);
    }

    public List<Map<String, Object>> assignments(String school, String session, String staff) {
        return jdbc.queryForList("SELECT a.Assignment_id AS id,a.Assign_id AS assignId,a.Message AS message,a.FilePath AS filePath,a.PostedDate AS postedDate,a.SubmissionDate AS submissionDate,a.class_name AS className,a.subject_id AS subjectId,COALESCE(sm.Subject,a.subject_id) AS subject,a.Stu_id AS studentId,a.status FROM TB_AssignmentMaster a LEFT JOIN TB_SubjectMaster sm ON sm.id=TRY_CONVERT(int,a.subject_id) AND sm.school_id=a.school_id WHERE a.school_id=? AND a.session=? AND ISNULL(a.status,1)=1 ORDER BY a.PostedDate DESC,a.Assignment_id DESC", school, session);
    }

    public List<Map<String, Object>> dailyHomeworkView(String school, String session, String secId, String date) {
        return jdbc.queryForList("SELECT dh.id AS homeworkId,COALESCE(sm.Subject,CONVERT(varchar(50),dh.subject_id)) AS subject,dh.homework_desc AS homeworkDesc,dh.file_path AS filePath,dh.created_by AS createdBy FROM daily_homework dh LEFT JOIN TB_SubjectMaster sm ON sm.id=dh.subject_id AND sm.school_id=dh.school_id WHERE dh.school_id=? AND dh.session=? AND dh.sec_id=? AND (dh.homework_date=? OR TRY_CONVERT(date,dh.homework_date)=TRY_CONVERT(date,?)) ORDER BY subject,dh.id", school, session, secId, date, date);
    }

    public List<Map<String, Object>> dailyHomeworkSubjects(String school, String session, String secId, String classId, String date) {
        return jdbc.queryForList("SELECT DISTINCT x.subjectId,x.subjectName,COALESCE(dh.homework_desc,'') AS homeworkDesc,COALESCE(dh.file_path,'') AS filePath,dh.id AS homeworkId FROM (SELECT cs.subject_id AS subjectId,COALESCE(NULLIF(sm.Subject,''),cs.subject_code) AS subjectName FROM TB_ClassSubject cs LEFT JOIN TB_SubjectMaster sm ON sm.id=cs.subject_id AND sm.school_id=cs.school_id WHERE cs.school_id=? AND cs.session=? AND cs.class_id=? AND ISNULL(cs.status,1)=1) x LEFT JOIN daily_homework dh ON dh.school_id=? AND dh.session=? AND dh.sec_id=? AND dh.subject_id=x.subjectId AND (dh.homework_date=? OR TRY_CONVERT(date,dh.homework_date)=TRY_CONVERT(date,?)) ORDER BY x.subjectName", school, session, classId, school, session, secId, date, date);
    }

    public List<Map<String, Object>> homeworkDates(String school, String session, String secId) {
        return jdbc.queryForList("SELECT homeworkDate FROM (SELECT homework_date AS homeworkDate,MAX(TRY_CONVERT(date,homework_date)) OVER (PARTITION BY homework_date) AS sortDate FROM daily_homework WHERE school_id=? AND session=? AND sec_id=?) x GROUP BY homeworkDate,sortDate ORDER BY sortDate DESC", school, session, secId);
    }

    public List<Map<String, Object>> homeworkDetails(String school, String session, String secId, String date) {
        return jdbc.queryForList("SELECT DISTINCT dh.homework_desc AS homeworkDesc,COALESCE(sm.Subject,CONVERT(varchar(50),dh.subject_id)) AS subject FROM daily_homework dh LEFT JOIN TB_SubjectMaster sm ON sm.id=dh.subject_id AND sm.school_id=dh.school_id WHERE dh.school_id=? AND dh.session=? AND dh.sec_id=? AND (dh.homework_date=? OR TRY_CONVERT(date,dh.homework_date)=TRY_CONVERT(date,?)) ORDER BY subject", school, session, secId, date, date);
    }

    public List<Map<String, Object>> syllabusClasses(String school, String session, String staff) {
        return jdbc.queryForList("SELECT u.class_id AS classId,COALESCE(cn.class_name,CONVERT(varchar(20),u.class_id)) AS className FROM upload_syllabus u LEFT JOIN class_name cn ON cn.id=TRY_CONVERT(int,u.class_id) AND cn.school_id=u.school_id WHERE u.school_id=? AND u.session=? AND ISNULL(u.status,1)=1 GROUP BY u.class_id,cn.class_name ORDER BY TRY_CONVERT(int,u.class_id),className", school, session);
    }

    public List<Map<String, Object>> syllabusTitles(String school, String session, String classId) {
        return jdbc.queryForList("SELECT syllabus_id AS id,title,REPLACE(file_path,'~/','') AS filePath,sec_id AS secId,term_id AS termId,exam_type_id AS examTypeId FROM upload_syllabus WHERE school_id=? AND session=? AND class_id=? AND ISNULL(status,1)=1 ORDER BY syllabus_id", school, session, classId);
    }

    public List<Map<String, Object>> assignmentReportSections(String school, String session, String staff) {
        return classes(school, session, staff);
    }

    public List<Map<String, Object>> assignmentReportAll(String school, String session) {
        return jdbc.queryForList("SELECT c.sec_id AS secId,c.class_id AS classId,cn.class_name+' ('+c.section+')' AS label,COALESCE(sm.Subject,CONVERT(varchar(50),cs.subject_id)) AS subject,cs.subject_id AS subjectId,COUNT(DISTINCT a.Assign_id) AS assignmentCount FROM tb_class c JOIN class_name cn ON cn.id=c.class_id AND cn.school_id=c.school_id JOIN TB_ClassSubject cs ON cs.school_id=c.school_id AND cs.session=? AND cs.class_id=c.class_id AND ISNULL(cs.status,1)=1 LEFT JOIN TB_SubjectMaster sm ON sm.id=cs.subject_id AND sm.school_id=cs.school_id LEFT JOIN TB_StudentMaster st ON st.school_id=c.school_id AND st.session=? AND st.sec_id=c.sec_id AND LOWER(st.status)='registered' LEFT JOIN TB_AssignmentMaster a ON a.school_id=c.school_id AND a.session=? AND a.sec_id=CONVERT(varchar(50),c.sec_id) AND TRY_CONVERT(int,a.subject_id)=cs.subject_id AND a.Stu_id=st.stu_id AND ISNULL(a.status,1)=1 WHERE c.school_id=? GROUP BY c.sec_id,c.class_id,c.section,cn.class_name,cs.subject_id,sm.Subject,cs.subject_code,cs.subject_order ORDER BY c.class_id,c.section,ISNULL(cs.subject_order,0),subject", session, session, session, school);
    }

    public List<Map<String, Object>> assignmentReportDetails(String school, String session, String secId) {
        return jdbc.queryForList("SELECT COALESCE(sm.Subject,CONVERT(varchar(50),cs.subject_id)) AS subject,cs.subject_id AS subjectId,COUNT(DISTINCT a.Assign_id) AS assignmentCount FROM TB_ClassSubject cs LEFT JOIN TB_SubjectMaster sm ON sm.id=cs.subject_id AND sm.school_id=cs.school_id LEFT JOIN TB_StudentMaster st ON st.school_id=cs.school_id AND st.session=? AND st.sec_id=? AND LOWER(st.status)='registered' LEFT JOIN TB_AssignmentMaster a ON a.school_id=cs.school_id AND a.session=? AND a.sec_id=? AND TRY_CONVERT(int,a.subject_id)=cs.subject_id AND a.Stu_id=st.stu_id AND ISNULL(a.status,1)=1 WHERE cs.school_id=? AND cs.session=? AND cs.class_id=(SELECT TOP 1 class_id FROM tb_class WHERE school_id=? AND sec_id=?) AND ISNULL(cs.status,1)=1 GROUP BY cs.subject_id,sm.Subject,cs.subject_code,cs.subject_order ORDER BY ISNULL(cs.subject_order,0),subject", session, secId, session, secId, school, session, school, secId);
    }

    public List<Map<String, Object>> mobileCoverageDetails(String school, String session, String classId, String secId, String status) {
        String statusSql = "";
        if ("logged_in".equalsIgnoreCase(status)) statusSql = " HAVING COUNT(DISTINCT us.session_id)>0 ";
        if ("not_logged_in".equalsIgnoreCase(status)) statusSql = " HAVING COUNT(DISTINCT us.session_id)=0 ";
        String sql = "SELECT st.registration_no AS registrationNo,UPPER(LTRIM(RTRIM(COALESCE(so.First_name,'')+' '+COALESCE(so.Mid_name,'')+' '+COALESCE(so.Last_name,'')))) AS studentName,so.Mother_name AS motherName,so.Father_name AS fatherName,so.Mobile AS mobileNo,so.Phone AS phoneNo,cn.class_name AS className,c.section AS sectionName,MAX(us.login_time) AS latestLoginTime,CASE WHEN COUNT(DISTINCT us.session_id)>0 THEN 'Yes' ELSE 'No' END AS Student,CASE WHEN COUNT(DISTINCT us.session_id)>0 THEN 'Yes' ELSE 'No' END AS Parent FROM TB_StudentMaster st LEFT JOIN student_other_info so ON so.school_id=st.school_id AND so.Registration_NO=st.Registration_NO LEFT JOIN class_name cn ON cn.school_id=st.school_id AND cn.id=st.class_id LEFT JOIN tb_class c ON c.school_id=st.school_id AND c.sec_id=st.sec_id LEFT JOIN UserSessions us ON us.school_id=st.school_id AND us.is_active=1 AND (LTRIM(RTRIM(us.user_id))=CONVERT(varchar(50),st.stu_id) OR EXISTS (SELECT 1 FROM student_other_info sib WHERE sib.school_id=st.school_id AND sib.SiblingId IS NOT NULL AND sib.SiblingId=so.SiblingId AND EXISTS (SELECT 1 FROM TB_StudentMaster sx WHERE sx.school_id=sib.school_id AND sx.Registration_NO=sib.Registration_NO AND LTRIM(RTRIM(us.user_id))=CONVERT(varchar(50),sx.stu_id)))) WHERE st.school_id=? AND st.session=? AND LOWER(st.status)='registered' AND (?='' OR st.class_id=TRY_CONVERT(int,?)) AND (?='' OR st.sec_id=TRY_CONVERT(int,?)) GROUP BY st.registration_no,so.First_name,so.Mid_name,so.Last_name,so.Mother_name,so.Father_name,so.Mobile,so.Phone,cn.class_name,c.section,st.stu_id" + statusSql + " ORDER BY className,sectionName,studentName";
        return jdbc.queryForList(sql, school, session, classId, classId, secId, secId);
    }

    public List<Map<String, Object>> mobileCoverageClass(String school, String session) {
        return jdbc.queryForList("SELECT cn.id AS classId,cn.class_name AS className,COUNT(DISTINCT st.stu_id) AS totalStudents,COUNT(DISTINCT CASE WHEN us.session_id IS NOT NULL THEN st.stu_id END) AS appInstalled,COUNT(DISTINCT st.stu_id)-COUNT(DISTINCT CASE WHEN us.session_id IS NOT NULL THEN st.stu_id END) AS appNotInstalled,CAST(CASE WHEN COUNT(DISTINCT st.stu_id)=0 THEN 0 ELSE COUNT(DISTINCT CASE WHEN us.session_id IS NOT NULL THEN st.stu_id END)*100.0/COUNT(DISTINCT st.stu_id) END AS decimal(5,2)) AS installedPercentage FROM class_name cn LEFT JOIN TB_StudentMaster st ON st.school_id=? AND st.session=? AND st.class_id=cn.id AND LOWER(st.status)='registered' LEFT JOIN UserSessions us ON us.school_id=st.school_id AND us.is_active=1 AND us.user_id=CONVERT(varchar(50),st.stu_id) WHERE cn.school_id=? GROUP BY cn.id,cn.class_name ORDER BY cn.id", school, session, school);
    }

    public long nextNumeric(String table, String column, String school) {
        Integer n = jdbc.queryForObject("SELECT ISNULL(MAX(TRY_CONVERT(int," + column + ")),0)+1 FROM " + table + " WHERE school_id=?", Integer.class, school);
        return n == null ? 1 : n;
    }

    public static boolean blank(String value) { return value == null || value.trim().isEmpty(); }
}
