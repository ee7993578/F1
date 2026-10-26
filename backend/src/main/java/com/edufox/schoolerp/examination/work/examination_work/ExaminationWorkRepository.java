package com.edufox.schoolerp.examination.work.examination_work;
import com.edufox.schoolerp.examination.report.examination_report.*;
import com.edufox.schoolerp.examination.setup.exam_setup.*;
import com.edufox.schoolerp.examination.update.delete_marks.*;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Direct-SQL implementation of the Examination/Work pages.
 * The legacy procedures were used only as a behavioural/schema reference;
 * this repository deliberately does not execute procedures.
 */
@Repository
@RequiredArgsConstructor
public class ExaminationWorkRepository {
    private final JdbcTemplate jdbc;

    public Map<String, Object> bootstrap(String school, String staffUid) {
        String session = defaultSession(school);
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("session", session);
        out.put("sessions", safe("SELECT session,status FROM TB_Session WHERE school_id=? ORDER BY id DESC", school));
        out.put("classes", classes(school, session, staffUid));
        out.put("faculties", safe("SELECT F_id AS id,UPPER(REPLACE(COALESCE(First_name,'')+' '+COALESCE(Mid_name,'')+' '+COALESCE(Last_name,''),'  ',' ')) AS name FROM TB_FacultyMaster WHERE school_id=? ORDER BY name", school));
        out.put("terms", terms(school, session, null));
        out.put("examTypes", safe("SELECT DISTINCT exam_type_id AS id,exam_type AS name FROM exam_type WHERE school_id=? ORDER BY exam_type_id", school));
        out.put("subjects", safe("SELECT id AS id,Subject AS name,Subject_code AS code FROM TB_SubjectMaster WHERE school_id=? AND (status IS NULL OR status=1) ORDER BY Subject", school));
        out.put("indicatorCategories", reportcardCategories(school, session));
        out.put("examIndicatorCategories", safe("SELECT id AS id,indicator_category AS name FROM tb_indicatorCAT WHERE school_id=? AND status='active' ORDER BY COALESCE(indicatorCATOrder,id)", school));
        out.put("indicators", safe("SELECT ID AS id,indicator_Name AS name,indicator_cat AS categoryId,indicator_category_id AS categoryId2 FROM tb_indicator WHERE school_id=? AND status='active' ORDER BY COALESCE(IndicatorOrder,ID)", school));
        out.put("reportcardCategories", reportcardCategories(school, session));
        out.put("learnerAttributes", safe("SELECT ATTRIBUTE_ID AS id,ATTRIBUTE_NAME AS name,HasAttributeValue AS hasValues FROM TB_LERNERPROFILE_ATTRIBUTE WHERE SCHOOL_ID=? AND SESSION=? AND STATUS='active' ORDER BY COALESCE(ATTR_ORDER,ATTRIBUTE_ID)", school, session));
        out.put("bloodGroups", safe("SELECT id,blood_group AS name FROM blood_group ORDER BY id", new Object[0]));
        out.put("houses", safe("SELECT House_id AS id,H_Name AS name FROM HouseMaster WHERE school_id=? AND (status IS NULL OR status=1) ORDER BY H_Name", school));
        out.put("remarks", safe("SELECT id,remark FROM tb_remarks WHERE school_id=? ORDER BY id", school));
        out.put("remarkTypes", List.of(
            Map.of("id", "1", "name", "Remark"),
            Map.of("id", "3", "name", "Exam Wise Remark"),
            Map.of("id", "4", "name", "Promoted Class")
        ));
        out.put("mappingTypes", safe("SELECT id,mapping_type AS name FROM marks_mapping_type ORDER BY id"));
        out.put("grades", safe("SELECT grade AS id,grade AS name FROM Subject_Grade WHERE school_id=? ORDER BY grade_order", school));
        return out;
    }

    public String defaultSession(String school) {
        List<Map<String, Object>> r = safe("SELECT TOP 1 session FROM TB_Session WHERE school_id=? AND status='Default' ORDER BY id DESC", school);
        if (!r.isEmpty()) return text(r.get(0).get("session"));
        r = safe("SELECT TOP 1 session FROM TB_Session WHERE school_id=? ORDER BY id DESC", school);
        return r.isEmpty() ? "" : text(r.get(0).get("session"));
    }

    public List<Map<String, Object>> classes(String school, String session, String staffUid) {
        // Keep the legacy class/section binding shape. Teacher restriction is applied when a mapping exists.
        List<Map<String, Object>> all = safe("""
            SELECT DISTINCT c.sec_id AS sectionId,c.class_id AS classId,c.section AS sectionName,
                   cn.class_name AS className,cn.class_group AS classGroup,cn.class_name+' ('+c.section+')' AS label
            FROM TB_Class c JOIN class_name cn ON cn.school_id=c.school_id AND cn.id=c.class_id
            WHERE c.school_id=? ORDER BY c.class_id,c.sec_id
            """, school);
        if (staffUid == null || staffUid.isBlank() || all.isEmpty()) return all;
        Integer faculty = jdbc.query("SELECT TOP 1 f_id FROM TB_FacultyMaster WHERE school_id=? AND staff_u_id=?", (rs, n) -> rs.getInt(1), school, staffUid).stream().findFirst().orElse(0);
        if (scalarInt("SELECT COUNT(*) FROM desig_mapping WHERE school_id=? AND (staff_u_id=? OR f_id=?) AND desg_id IN (28,39)", school, staffUid, faculty) > 0) return all;
        List<Map<String, Object>> mapped = safe("""
            SELECT DISTINCT c.sec_id AS sectionId,c.class_id AS classId,c.section AS sectionName,
                   cn.class_name AS className,cn.class_group AS classGroup,cn.class_name+' ('+c.section+')' AS label
            FROM TB_Class c JOIN class_name cn ON cn.school_id=c.school_id AND cn.id=c.class_id
            JOIN Tb_ClassTeacher ct ON ct.school_id=c.school_id AND ct.section_id=c.sec_id AND ct.session=?
              AND ct.staff_u_id=?
            WHERE c.school_id=? ORDER BY c.class_id,c.sec_id
            """, session, staffUid, school);
        return mapped.isEmpty() ? all : mapped;
    }

    public List<Map<String, Object>> terms(String school, String session, Integer classId) {
        if (classId == null || classId == 0) {
            return safe("""
                SELECT DISTINCT st.term AS id,COALESCE(t.term_name,CONVERT(varchar(20),st.term)) AS name
                FROM session_term st LEFT JOIN term t ON t.term_id=st.term
                WHERE st.school_id=? AND st.Session=? ORDER BY st.term
                """, school, session);
        }
        return safe("""
            SELECT DISTINCT e.term_id AS id,
                   COALESCE(t.term_name,CONVERT(varchar(20),e.term_id)) AS name
            FROM exam_set e
            JOIN assign_exam_set a ON a.school_id=e.school_id AND a.exam_set_id=e.exam_set_id
            LEFT JOIN term t ON t.term_id=e.term_id
            WHERE e.school_id=? AND a.class_id=?
              AND TRY_CONVERT(int,a.from_year)<=TRY_CONVERT(int,LEFT(?,4))
              AND (CONVERT(varchar(20),a.to_year)='0'
                   OR TRY_CONVERT(int,a.to_year)>=TRY_CONVERT(int,RIGHT(?,4)))
            ORDER BY e.term_id
            """, school, classId, session, session);
    }

    public List<Map<String, Object>> subjects(String school, String session, int sectionId, String facultyId) {
        return safe("""
            SELECT DISTINCT sm.id AS id,sm.Subject AS name,sm.Subject_code AS code,
                   COALESCE(CONVERT(varchar(20),cs.isoptional),'0') AS optional,
                   COALESCE(CONVERT(varchar(20),cs.isgrade),'0') AS grade,
                   COALESCE(CONVERT(varchar(20),cs.isprac),'0') AS practical,cs.subject_order AS displayOrder
            FROM TB_Class c
            JOIN TB_ClassSubject cs ON cs.school_id=c.school_id AND cs.class_id=c.class_id
                AND (cs.status IS NULL OR CONVERT(varchar(20),cs.status) IN ('1','active','true','Y'))
            CROSS APPLY (SELECT TOP 1 x.id,x.Subject,x.Subject_code,x.status
                         FROM TB_SubjectMaster x WHERE x.id=cs.subject_id
                           AND (x.status IS NULL OR CONVERT(varchar(20),x.status) IN ('1','active','true','Y'))
                         ORDER BY CASE WHEN x.school_id=cs.school_id THEN 0 ELSE 1 END,x.row_id DESC) sm
            WHERE c.school_id=? AND c.sec_id=? AND (cs.session=? OR cs.session IS NULL OR cs.session='')
              AND (sm.status IS NULL OR CONVERT(varchar(20),sm.status) IN ('1','active','true','Y'))
            ORDER BY displayOrder,name
            """, school, sectionId, session);
    }

    public List<Map<String, Object>> exams(String school, String session, int sectionId, String termId) {
        int classId = scalarInt("SELECT TOP 1 class_id FROM TB_Class WHERE school_id=? AND sec_id=?", school, sectionId);
        return safe("""
            SELECT e.exam_type_id AS id,e.exam_name_dis AS name,et.exam_id AS examId,
                   e.exam_order AS displayOrder,
                   COALESCE((SELECT TOP 1 w.weightage FROM TB_WeightageMarks_new w
                     WHERE w.school_id=e.school_id AND w.session=? AND w.class_id=? AND w.term=? AND w.exam_type_id=e.exam_type_id),'') AS weightage
            FROM exam_set e
            JOIN exam_type et ON et.school_id=e.school_id AND et.exam_type_id=e.exam_type_id
            WHERE e.school_id=? AND e.term_id=?
              AND e.exam_set_id=(SELECT TOP 1 a.exam_set_id FROM assign_exam_set a
                WHERE a.school_id=e.school_id AND a.class_id=?
                  AND TRY_CONVERT(int,a.from_year)<=TRY_CONVERT(int,LEFT(?,4))
                  AND (CONVERT(varchar(20),a.to_year)='0' OR TRY_CONVERT(int,a.to_year)>=TRY_CONVERT(int,RIGHT(?,4))))
            ORDER BY COALESCE(e.exam_order,9999),e.exam_type_id
            """, session, classId, termId, school, termId, classId, session, session);
    }

    public Map<String, Object> markOptions(String school, String session, int sectionId, String termId, String examTypeId, String subjectId) {
        int classId = scalarInt("SELECT TOP 1 class_id FROM TB_Class WHERE school_id=? AND sec_id=?", school, sectionId);
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("classId", classId);
        out.put("limits", safe("""
            SELECT TOP 1 min_marks AS minMarks,max_marks AS maxMarks,Grade_subject AS gradeSubject,
                   Exam_id AS examId,Exam_type_id AS examTypeId,Term_id AS termId
            FROM tb_exam_max_min_new
            WHERE school_id=? AND Session=? AND class_id=? AND (sec_id=? OR sec_id IS NULL)
              AND subject_id=? AND Exam_type_id=? AND Term_id=?
            ORDER BY id DESC
            """, school, session, classId, sectionId, subjectId, examTypeId, termId));
        out.put("weightage", safe("""
            SELECT TOP 1 weightage,exam_id AS examId,exam_type_id AS examTypeId,term
            FROM TB_WeightageMarks_new
            WHERE school_id=? AND session=? AND class_id=? AND exam_type_id=? AND term=?
            ORDER BY id DESC
            """, school, session, classId, examTypeId, termId));
        return out;
    }

    public List<Map<String, Object>> learnerValues(String school, String session, String attributeId) {
        return safe("""
            SELECT v.id AS rowId,v.ATTRIBUTE_ID AS attributeId,v.Attribute_Value_Id AS id,
                   v.Attribute_Value AS name
            FROM LERNERPROFILE_ATTRIBUTE_Value v
            WHERE v.school_id=? AND v.session=? AND v.ATTRIBUTE_ID=? AND v.status=1
            ORDER BY v.Attribute_Value
            """, school, session, attributeId);
    }

    public List<Map<String, Object>> reportcardCategories(String school, String session) {
        return safe("SELECT id,CategoryName AS name FROM ReportCardCategory WHERE school_id=? AND session=? AND (status IS NULL OR status=1) ORDER BY COALESCE(CategoryOrder,id)", school, session);
    }
    public List<Map<String, Object>> reportcardSubcategories(String school, String session, String categoryId) {
        return safe("SELECT id,CategoryName AS name,HasSubcategoryValue AS hasValues FROM ReportCardSubCategory WHERE school_id=? AND session=? AND CategoryId=? AND (status IS NULL OR status=1) ORDER BY COALESCE(SubCategoryOrder,id)", school, session, categoryId);
    }
    public List<Map<String, Object>> reportcardValues(String school, String session, String subcategoryId) {
        return safe("SELECT id,subcategoryValue AS name FROM reportcardsubcategoryValue WHERE school_id=? AND session=? AND subcategoryid=? AND (status IS NULL OR status=1) ORDER BY subcategoryValue", school, session, subcategoryId);
    }

    public List<Map<String, Object>> marks(String school, String session, int sectionId, String termId, String examTypeId, String subjectId) {
        return safe("""
            SELECT s.stu_id AS studentId,s.registration_no AS registrationNo,
              UPPER(REPLACE(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,''),'  ',' ')) AS studentName,
              COALESCE(CONVERT(varchar(40),sm.marks),CONVERT(varchar(40),m.Marks),'') AS marks,
              COALESCE(CONVERT(varchar(40),sm.marks),'') AS grade,
              COALESCE(sm.Absentism,m.Absentism,'') AS absentism,
              COALESCE(CONVERT(varchar(40),sm.min_marks),CONVERT(varchar(40),m.min_marks),'') AS minMarks,
              COALESCE(CONVERT(varchar(40),sm.max_marks),CONVERT(varchar(40),m.max_marks),'') AS maxMarks,
              COALESCE(CONVERT(varchar(40),sm.Weightage),'') AS weightage,
              COALESCE(CONVERT(varchar(40),sm.Weightage_Marks),'') AS weightageMarks
            FROM TB_StudentMaster s
            LEFT JOIN student_other_info so ON so.school_id=s.school_id AND so.registration_no=s.registration_no
            LEFT JOIN senior_marks sm ON sm.school_id=s.school_id AND sm.Stu_id=s.stu_id AND sm.Session=s.session
              AND sm.sec_id=s.sec_id AND sm.Subject_id=? AND sm.Term_id=? AND sm.Exam_type_id=?
            LEFT JOIN marks_entry m ON m.school_id=s.school_id AND m.Stu_id=s.stu_id AND m.Session=s.session
              AND m.sec_id=s.sec_id AND m.Subject_id=? AND m.term_id=? AND m.exam_type_id=?
            WHERE s.school_id=? AND s.session=? AND s.sec_id=? AND LOWER(s.status)='registered'
            ORDER BY UPPER(REPLACE(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,''),'  ',' '))
            """, subjectId, termId, examTypeId, subjectId, termId, examTypeId, school, session, sectionId);
    }

    public void saveMarks(String school, String by, Map<String, Object> body) {
        String session = text(body.get("session")); int sectionId = integer(body.get("sectionId"));
        String term = text(body.get("termId")), exam = text(body.get("examTypeId")), subject = text(body.get("subjectId"));
        int classId = scalarInt("SELECT TOP 1 class_id FROM TB_Class WHERE school_id=? AND sec_id=?", school, sectionId);
        String faculty = text(body.get("facultyId"));
        String examId = text(body.get("examId"));
        if (examId.isBlank()) examId = scalarText("SELECT TOP 1 Exam_id FROM tb_exam_max_min_new WHERE school_id=? AND Session=? AND class_id=? AND (sec_id=? OR sec_id IS NULL) AND subject_id=? AND Exam_type_id=? AND Term_id=? ORDER BY id DESC", school, session, classId, sectionId, subject, exam, term);
        String weightage = text(body.get("weightage"));
        if (weightage.isBlank()) weightage = scalarText("SELECT TOP 1 weightage FROM TB_WeightageMarks_new WHERE school_id=? AND session=? AND class_id=? AND exam_type_id=? AND term=? ORDER BY id DESC", school, session, classId, exam, term);
        for (Map<String, Object> row : rows(body)) {
            int stu = integer(row.get("studentId")); if (stu <= 0) continue;
            String marks = text(row.get("marks"));
            if ("Grade".equalsIgnoreCase(text(body.get("entryType"))) && !text(row.get("grade")).isBlank()) marks = text(row.get("grade"));
            String absent = text(row.get("absentism"));
            String min = text(row.get("minMarks"));
            String max = text(row.get("maxMarks"));
            String weightageMarks = weightedMarks(marks, max, weightage, absent);
            jdbc.update("DELETE FROM senior_marks WHERE school_id=? AND Stu_id=? AND Session=? AND sec_id=? AND Subject_id=? AND Term_id=? AND Exam_type_id=?", school, stu, session, sectionId, subject, term, exam);
            jdbc.update("""
                INSERT INTO senior_marks(Stu_id,sec_id,Subject_id,Exam_type_id,Session,marks,min_marks,max_marks,Term_id,F_id,school_id,created_by,created_date,absentism,Weightage,Weightage_Marks,exam_id)
                VALUES(?,?,?,?,?,?,?,?,?,?,?, ?,GETDATE(),?,?,?,?)
                """, stu, sectionId, subject, exam, session, nullable(marks), nullable(min), nullable(max), term,
                nullable(faculty), school, by, absent, nullable(weightage), nullable(weightageMarks), nullable(examId));
        }
        recalculateSeniorMarks(school, session, sectionId, subject, term, examId, weightage);
    }

    /** Direct SQL equivalent of the legacy calculatedAvgMarksEntry/marks_calculated tail. */
    private void recalculateSeniorMarks(String school,String session,int sectionId,String subject,String term,String examId,String weightage) {
        if (examId.isBlank()) return;
        double w;
        try { w=Double.parseDouble(weightage); } catch (Exception e) { return; }
        jdbc.update("""
            WITH calculated AS (
              SELECT sm.stu_id,sm.exam_id,
                SUM(CASE WHEN TRY_CONVERT(decimal(18,2),sm.Weightage_Marks) IS NULL THEN 0 ELSE TRY_CONVERT(decimal(18,2),sm.Weightage_Marks) END) * ? /
                  CASE WHEN SUM(CASE WHEN TRY_CONVERT(decimal(18,2),sm.Weightage) IS NULL THEN 0 ELSE TRY_CONVERT(decimal(18,2),sm.Weightage) END)=0 THEN 1
                       ELSE SUM(CASE WHEN TRY_CONVERT(decimal(18,2),sm.Weightage) IS NULL THEN 0 ELSE TRY_CONVERT(decimal(18,2),sm.Weightage) END) END AS calculatedMarks,
                MAX(CASE WHEN TRY_CONVERT(decimal(18,2),sm.Weightage) IS NULL THEN 0 ELSE TRY_CONVERT(decimal(18,2),sm.Weightage) END) AS calculatedMax
              FROM senior_marks sm
              JOIN TB_StudentMaster st ON st.school_id=sm.school_id AND st.stu_id=sm.stu_id AND st.session=sm.session AND st.sec_id=?
              WHERE sm.school_id=? AND sm.session=? AND sm.subject_id=? AND sm.term_id=? AND sm.exam_id=?
              GROUP BY sm.stu_id,sm.exam_id
            )
            UPDATE sm SET calculated_marks=CONVERT(varchar(40),CONVERT(decimal(18,2),c.calculatedMarks)),
                          calculated_maxmarks=CONVERT(varchar(40),CONVERT(decimal(18,2),c.calculatedMax))
            FROM senior_marks sm JOIN calculated c ON c.stu_id=sm.stu_id AND c.exam_id=sm.exam_id
            WHERE sm.school_id=? AND sm.session=? AND sm.subject_id=? AND sm.term_id=? AND sm.exam_id=?
            """, w, sectionId, school, session, subject, term, examId, school, session, subject, term, examId);
    }

    public Map<String, Object> preprimaryBootstrap(String school, String session, int sectionId, String staffUid, String mode) {
        int classId = scalarInt("SELECT TOP 1 class_id FROM TB_Class WHERE school_id=? AND sec_id=?", school, sectionId);
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("classId", classId);
        if ("mapping".equalsIgnoreCase(text(mode))) out.put("classes", mappingClasses(school, session, staffUid));
        out.put("headers", preprimaryHeaders(school, session, sectionId));
        out.put("students", preprimaryStudents(school, session, sectionId));
        out.put("terms", terms(school, session, classId));
        out.put("examTypes", safe("SELECT DISTINCT exam_type_id AS id,exam_type AS name FROM exam_type WHERE school_id=? ORDER BY exam_type_id", school));
        String classType=scalarText("SELECT TOP 1 COALESCE(ClassType,'Regular') FROM TB_Class WHERE school_id=? AND sec_id=?",school,sectionId);
        List<Map<String,Object>> grades=new ArrayList<>(safe("SELECT grade AS id,grade AS name FROM chk_grade_primary WHERE school_id=? AND (ClassType=? OR ClassType IS NULL) ORDER BY GradeOrder",school,classType));
        for(String grade:List.of("AB","NA","ML")) if(grades.stream().noneMatch(item->grade.equalsIgnoreCase(text(item.get("id"))))) grades.add(Map.of("id",grade,"name",grade));
        out.put("grades",grades);
        return out;
    }

    private List<Map<String,Object>> mappingClasses(String school, String session, String staffUid) {
        String group = scalarInt("SELECT COUNT(*) FROM class_name WHERE school_id=? AND class_group='Primary'", school) > 0 ? "Primary" : "Junior";
        List<Map<String,Object>> all = safe("""
            SELECT DISTINCT c.sec_id AS sectionId,c.class_id AS classId,c.section AS sectionName,
                   cn.class_name AS className,cn.class_group AS classGroup,cn.class_name+' ('+c.section+')' AS label
            FROM TB_Class c JOIN class_name cn ON cn.school_id=c.school_id AND cn.id=c.class_id
            WHERE c.school_id=? AND cn.class_group=? ORDER BY c.class_id,c.sec_id
            """, school, group);
        int faculty = jdbc.query("SELECT TOP 1 f_id FROM TB_FacultyMaster WHERE school_id=? AND staff_u_id=?", (rs,n)->rs.getInt(1), school, staffUid).stream().findFirst().orElse(0);
        if (scalarInt("SELECT COUNT(*) FROM desig_mapping WHERE school_id=? AND (staff_u_id=? OR f_id=?) AND desg_id IN (28,37)", school, staffUid, faculty) > 0) return all;
        List<Map<String,Object>> mapped = safe("""
            SELECT DISTINCT c.sec_id AS sectionId,c.class_id AS classId,c.section AS sectionName,
                   cn.class_name AS className,cn.class_group AS classGroup,cn.class_name+' ('+c.section+')' AS label
            FROM TB_Class c JOIN class_name cn ON cn.school_id=c.school_id AND cn.id=c.class_id
            JOIN faculty_subject_mapping f ON f.school_id=c.school_id AND f.sec_id=c.sec_id
            WHERE c.school_id=? AND cn.class_group=? AND f.staff_u_id=? AND f.session=?
            ORDER BY c.class_id,c.sec_id
            """, school, group, staffUid, session);
        return mapped;
    }

    public List<Map<String, Object>> preprimaryHeaders(String school, String session, int sectionId) {
        int classId = scalarInt("SELECT TOP 1 class_id FROM TB_Class WHERE school_id=? AND sec_id=?", school, sectionId);
        return safe("""
            SELECT DISTINCT h.header_id AS id,h.header_name AS name,h.header_order AS displayOrder
            FROM header_name h
            WHERE h.school_id=? AND h.session=? AND (h.ClassType IS NULL OR h.ClassType IN
              (SELECT TOP 1 COALESCE(ClassType,'Regular') FROM TB_Class WHERE school_id=? AND sec_id=?))
              AND EXISTS (SELECT 1 FROM category_name c WHERE c.school_id=h.school_id AND c.session=h.session AND c.header_id=h.header_id AND c.class_id=? AND (c.status IS NULL OR c.status=1))
            ORDER BY displayOrder,name
            """, school, session, school, sectionId, classId);
    }

    public List<Map<String, Object>> preprimaryCategories(String school, String session, int sectionId, String headerId) {
        int classId = scalarInt("SELECT TOP 1 class_id FROM TB_Class WHERE school_id=? AND sec_id=?", school, sectionId);
        return safe("""
            SELECT DISTINCT c.category_id AS id,c.category_name AS name,c.cat_order AS displayOrder
            FROM category_name c
            WHERE c.school_id=? AND c.session=? AND c.header_id=? AND c.class_id=?
              AND (c.status IS NULL OR c.status=1)
            ORDER BY displayOrder,name
            """, school, session, headerId, classId);
    }

    public List<Map<String, Object>> preprimaryStudents(String school, String session, int sectionId) {
        return safe("""
            SELECT s.stu_id AS studentId,s.registration_no AS registrationNo,
              UPPER(REPLACE(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,''),'  ',' ')) AS studentName,
              s.sec_id AS sectionId
            FROM TB_StudentMaster s JOIN student_other_info so ON so.school_id=s.school_id AND so.registration_no=s.registration_no
            WHERE s.school_id=? AND s.session=? AND s.sec_id=? AND LOWER(s.status)='registered'
            ORDER BY studentName
            """, school, session, sectionId);
    }

    public List<Map<String, Object>> preprimaryRows(String school, String session, int sectionId, String headerId, String categoryId, String termId, String examTypeId, String studentId) {
        if (headerId == null || headerId.isBlank() || categoryId == null || categoryId.isBlank()) return List.of();
        int classId = scalarInt("SELECT TOP 1 class_id FROM TB_Class WHERE school_id=? AND sec_id=?", school, sectionId);
        return safe("""
            SELECT s.stu_id AS studentId,s.registration_no AS registrationNo,
              UPPER(REPLACE(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,''),'  ',' ')) AS studentName,
              a.header_id AS headerId,a.category_id AS categoryId,a.attribute_id AS attributeId,
              a.attribute_name AS attributeName,
              COALESCE(CONVERT(varchar(40),pm.marks),'') AS marks,
              COALESCE(CONVERT(varchar(40),pm.grade),'') AS grade,
              COALESCE(pm.absentism,'0') AS absentism,
              COALESCE(CONVERT(varchar(40),mm.min_marks),'') AS minMarks,
              COALESCE(CONVERT(varchar(40),mm.max_marks),'') AS maxMarks,
              COALESCE(mm.grade_subject,'N') AS gradeSubject,
              cn.class_group AS classGroup,tc.ClassType AS classType
            FROM TB_StudentMaster s
            LEFT JOIN student_other_info so ON so.school_id=s.school_id AND so.registration_no=s.registration_no
            JOIN attribute_name a ON a.school_id=? AND a.session=? AND a.class_id=? AND a.header_id=? AND a.category_id=?
              AND (a.status IS NULL OR a.status=1)
            JOIN TB_Class tc ON tc.school_id=s.school_id AND tc.sec_id=s.sec_id
            JOIN class_name cn ON cn.school_id=s.school_id AND cn.id=?
            OUTER APPLY (SELECT TOP 1 p.marks,p.grade,p.absentism
                         FROM primary_marks p
                         WHERE p.school_id=s.school_id AND p.session=s.session AND p.stu_id=s.stu_id
                           AND p.header_id=a.header_id AND p.category_id=a.category_id AND p.attribute_id=a.attribute_id
                           AND p.exam_type_id=? ORDER BY p.created_date DESC) pm
            OUTER APPLY (SELECT TOP 1 x.min_marks,x.max_marks,x.grade_subject
                         FROM tb_exam_max_min_primary x
                         WHERE x.school_id=? AND x.session=? AND x.class_id=?
                           AND x.header_id=a.header_id AND x.category_id=a.category_id AND x.attribute_id=a.attribute_id
                           AND x.exam_type_id=? ORDER BY x.id DESC) mm
            WHERE s.school_id=? AND s.session=? AND s.sec_id=? AND LOWER(s.status)='registered'
              AND (?='' OR s.stu_id=?)
            ORDER BY studentName,COALESCE(a.attribute_order,9999),a.attribute_name
            """, school, session, classId, headerId, categoryId, classId, examTypeId,
            school, session, classId, examTypeId, school, session, sectionId, text(studentId), text(studentId));
    }

    public List<Map<String,Object>> preprimaryStudentRows(String school, String session, int sectionId, String studentId, String termId, String examTypeId) {
        int classId = scalarInt("SELECT TOP 1 class_id FROM TB_Class WHERE school_id=? AND sec_id=?", school, sectionId);
        return safe("""
            SELECT s.stu_id AS studentId,s.registration_no AS registrationNo,
              UPPER(REPLACE(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,''),'  ',' ')) AS studentName,
              h.header_id AS headerId,h.header_name AS headerName,c.category_id AS categoryId,c.category_name AS categoryName,
              a.attribute_id AS attributeId,a.attribute_name AS attributeName,
              COALESCE(CONVERT(varchar(40),pm.marks),'') AS marks,COALESCE(CONVERT(varchar(40),pm.grade),'') AS grade,
              COALESCE(pm.absentism,'0') AS absentism,COALESCE(CONVERT(varchar(40),mm.min_marks),'') AS minMarks,
              COALESCE(CONVERT(varchar(40),mm.max_marks),'') AS maxMarks,COALESCE(mm.grade_subject,'N') AS gradeSubject,
              cn.class_group AS classGroup,tc.ClassType AS classType
            FROM TB_StudentMaster s
            LEFT JOIN student_other_info so ON so.school_id=s.school_id AND so.registration_no=s.registration_no
            JOIN TB_Class tc ON tc.school_id=s.school_id AND tc.sec_id=s.sec_id
            JOIN class_name cn ON cn.school_id=tc.school_id AND cn.id=tc.class_id
            JOIN header_name h ON h.school_id=s.school_id AND h.session=? AND h.ClassType=COALESCE(tc.ClassType,'Regular')
            JOIN category_name c ON c.school_id=h.school_id AND c.session=h.session AND c.header_id=h.header_id AND c.class_id=tc.class_id AND c.ClassType=h.ClassType
              AND (c.status IS NULL OR c.status=1)
            JOIN attribute_name a ON a.school_id=c.school_id AND a.session=c.session AND a.class_id=tc.class_id AND a.header_id=c.header_id AND a.category_id=c.category_id
              AND (a.status IS NULL OR a.status=1)
            OUTER APPLY (SELECT TOP 1 p.marks,p.grade,p.absentism FROM primary_marks p
                         WHERE p.school_id=s.school_id AND p.session=s.session AND p.stu_id=s.stu_id
                           AND p.header_id=a.header_id AND p.category_id=a.category_id AND p.attribute_id=a.attribute_id
                           AND p.exam_type_id=? ORDER BY p.created_date DESC) pm
            OUTER APPLY (SELECT TOP 1 x.min_marks,x.max_marks,x.grade_subject FROM tb_exam_max_min_primary x
                         WHERE x.school_id=s.school_id AND x.session=s.session AND x.class_id=tc.class_id
                           AND x.header_id=a.header_id AND x.category_id=a.category_id AND x.attribute_id=a.attribute_id
                           AND x.exam_type_id=? ORDER BY x.id DESC) mm
            WHERE s.school_id=? AND s.session=? AND s.sec_id=? AND s.stu_id=? AND LOWER(s.status)='registered'
            ORDER BY TRY_CONVERT(int,h.header_order),h.header_name,COALESCE(c.cat_order,9999),c.category_name,COALESCE(a.attribute_order,9999),a.attribute_name
            """, session, examTypeId, examTypeId, school, session, sectionId, studentId);
    }

    public void savePreprimary(String school, String by, Map<String, Object> body) {
        String session=text(body.get("session")), term=text(body.get("termId")), exam=text(body.get("examTypeId"));
        List<Map<String,Object>> details=rows(body);
        if(details.isEmpty()) return;
        boolean studentWise="Student Wise".equalsIgnoreCase(text(body.get("mode")));
        Map<String,List<Map<String,Object>>> grouped=new LinkedHashMap<>();
        for(Map<String,Object> row:details) grouped.computeIfAbsent(text(row.get("headerId"))+"|"+text(row.get("categoryId")), k->new ArrayList<>()).add(row);
        for(List<Map<String,Object>> group:grouped.values()) {
            String header=text(group.get(0).get("headerId")),category=text(group.get(0).get("categoryId"));
            Map<String,List<Map<String,Object>>> byStudent=new LinkedHashMap<>();
            for(Map<String,Object> row:group) byStudent.computeIfAbsent(text(row.get("studentId")), k->new ArrayList<>()).add(row);
            for(Map.Entry<String,List<Map<String,Object>>> entry:byStudent.entrySet()) {
                String student=entry.getKey(); if(student.isBlank()) continue;
                jdbc.update("""
                    INSERT INTO primary_marks_log(stu_id,sec_id,header_id,category_id,attribute_id,marks,max_marks,grade,term,exam_type_id,session,absentism,created_date,created_by,modified_date,modified_by,school_id)
                    SELECT stu_id,sec_id,header_id,category_id,attribute_id,marks,max_marks,grade,term,exam_type_id,session,absentism,created_date,created_by,modified_date,modified_by,school_id
                    FROM primary_marks WHERE school_id=? AND session=? AND stu_id=? AND header_id=? AND category_id=? AND exam_type_id=?
                    """, school,session,student,header,category,exam);
                jdbc.update("DELETE FROM primary_marks WHERE school_id=? AND session=? AND stu_id=? AND header_id=? AND category_id=? AND exam_type_id=?", school,session,student,header,category,exam);
                for(Map<String,Object> row:entry.getValue()) {
                    String marks=text(row.get("marks")), grade=text(row.get("grade")), absent=text(row.get("absentism")), max=text(row.get("maxMarks"));
                    boolean gradeSubject="Y".equalsIgnoreCase(text(row.get("gradeSubject")));
                    if(gradeSubject){marks="0.00";max="0.00";boolean special="ML".equalsIgnoreCase(grade)||"NA".equalsIgnoreCase(grade)||"OD".equalsIgnoreCase(grade)||"AB".equalsIgnoreCase(grade);absent=special?grade:"";if(special) grade="";}
                    else { if("ML".equalsIgnoreCase(absent)||"NA".equalsIgnoreCase(absent)||"OD".equalsIgnoreCase(absent)) max="0.00"; if(!studentWise) grade=primaryGrade(school,session,marks,max,row.get("classGroup"),row.get("classType"),true); else grade=primaryGrade(school,session,marks,max,row.get("classGroup"),row.get("classType"),false); }
                    jdbc.update("""
                        INSERT INTO primary_marks(stu_id,sec_id,header_id,category_id,attribute_id,marks,max_marks,grade,term,exam_type_id,session,absentism,created_date,created_by,modified_date,modified_by,school_id)
                        VALUES(?,?,?,?,?,?,?,?,?,?,?, ?,GETDATE(),?,GETDATE(),?,?)
                        """,student,text(row.get("sectionId")),header,category,text(row.get("attributeId")),marks,max,grade,term,exam,session,absent,by,by,school);
                }
            }
        }
    }

    public List<Map<String, Object>> health(String school, String session, int sectionId, String term) {
        return safe("""
            SELECT s.House_id AS houseId,s.stu_id AS studentId,s.Registration_NO AS registrationNo,
              UPPER(REPLACE(COALESCE(so.First_Name,'')+' '+COALESCE(so.Mid_Name,'')+' '+COALESCE(so.Last_Name,''),'  ',' ')) AS studentName,
              COALESCE(h.Height,'') AS height,COALESCE(h.Weight,'') AS weight,COALESCE(so.bloodgroup,0) AS bloodGroup,
              COALESCE(h.Vision,0) AS vision,COALESCE(h.teeth,0) AS teeth,COALESCE(h.Oral_Hygiene,0) AS oralHygiene,
              COALESCE(h.Specificaliment,'') AS specificAilment
            FROM TB_StudentMaster s JOIN student_other_info so ON so.school_id=s.school_id AND so.registration_no=s.registration_no
            LEFT JOIN HealthStatus h ON h.school_id=s.school_id AND h.Stu_ID=s.stu_id AND h.session=s.session AND h.term_id=?
            WHERE s.school_id=? AND s.session=? AND s.sec_id=? AND LOWER(s.status)='registered'
            ORDER BY studentName
            """, term, school, session, sectionId);
    }

    public void saveHealth(String school, String by, Map<String, Object> body) {
        String session=text(body.get("session")), term=text(body.get("termId")); int sec=integer(body.get("sectionId"));
        for (Map<String,Object> row: rows(body)) {
            int stu=integer(row.get("studentId")); if(stu<=0)continue;
            jdbc.update("DELETE FROM HealthStatus WHERE school_id=? AND Stu_ID=? AND session=? AND term_id=?", school,stu,session,term);
            jdbc.update("INSERT INTO HealthStatus(Stu_ID,Height,Weight,teeth,Blood_Gp,Vision,Oral_Hygiene,Specificaliment,created_by,modified_date,modified_by,school_id,term_id,session) VALUES(?,?,?,?,?,?,?,?,?,GETDATE(),?,?,?,?)", stu,text(row.get("height")),text(row.get("weight")),text(row.get("teeth")),text(row.get("bloodGroup")),text(row.get("vision")),text(row.get("oralHygiene")),text(row.get("specificAilment")),by,"",by,school,term,session);
            jdbc.update("UPDATE TB_StudentMaster SET House_id=? WHERE school_id=? AND session=? AND stu_id=?", nullable(row.get("houseId")),school,session,stu);
            jdbc.update("UPDATE student_other_info SET bloodgroup=? WHERE school_id=? AND registration_no=(SELECT TOP 1 registration_no FROM TB_StudentMaster WHERE school_id=? AND session=? AND stu_id=?)", nullable(row.get("bloodGroup")),school,school,session,stu);
        }
    }

    public List<Map<String,Object>> indicatorGrades(String school,String session,int sectionId,String term,String indicatorId,String categoryId) {
        int classId=scalarInt("SELECT TOP 1 class_id FROM TB_Class WHERE school_id=? AND sec_id=?",school,sectionId);
        String category=categoryId.isBlank()?"":scalarText("SELECT TOP 1 indicator_category FROM tb_indicatorCAT WHERE school_id=? AND id=?",school,categoryId);
        if(category.isBlank()) category=categoryId;
        return safe("""
          SELECT s.stu_id AS studentId,s.registration_no AS registrationNo,
           UPPER(REPLACE(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,''),'  ',' ')) AS studentName,
           a.indicator_id AS indicatorId,a.indicator_name AS indicatorName,
           COALESCE(g.Grade,'') AS grade,COALESCE(g.remark,'') AS remark
          FROM TB_StudentMaster s
          LEFT JOIN student_other_info so ON so.school_id=s.school_id AND so.registration_no=s.registration_no
          JOIN tb_indicator a ON a.school_id=s.school_id AND a.session=? AND a.class_id=? AND a.status='active'
          LEFT JOIN tb_indicator_Grade g ON g.school_id=s.school_id AND g.session=s.session AND g.stu_id=s.stu_id
            AND g.indicator_ID=a.indicator_id AND g.Term_id=?
          WHERE s.school_id=? AND s.session=? AND s.sec_id=? AND LOWER(s.status)='registered'
            AND (?='' OR a.indicator_id=?) AND (?='' OR a.indicator_cat=?)
          ORDER BY studentName,COALESCE(a.IndicatorOrder,9999),a.indicator_name
          """,session,classId,term,school,session,sectionId,indicatorId,indicatorId,category,category);
    }
    public void saveIndicatorGrades(String school,String by,Map<String,Object> body) {
        String session=text(body.get("session")),term=text(body.get("termId"));
        for(Map<String,Object> r:rows(body)){
            String stu=text(r.get("studentId")),indicator=text(r.get("indicatorId")),grade=text(r.get("grade"));
            if(stu.isBlank()||indicator.isBlank()||grade.isBlank()||"select".equalsIgnoreCase(grade)) continue;
            int n=scalarInt("SELECT COUNT(*) FROM tb_indicator_Grade WHERE school_id=? AND session=? AND stu_id=? AND indicator_ID=? AND Term_id=?",school,session,stu,indicator,term);
            if(n>0) jdbc.update("UPDATE tb_indicator_Grade SET Grade=?,remark=? WHERE school_id=? AND session=? AND stu_id=? AND indicator_ID=? AND Term_id=?",grade,text(r.get("remark")),school,session,stu,indicator,term);
            else jdbc.update("INSERT INTO tb_indicator_Grade(school_id,session,indicator_ID,stu_id,Term_id,Grade,remark) VALUES(?,?,?,?,?,?,?)",school,session,indicator,stu,term,grade,text(r.get("remark")));
        }
    }

    public List<Map<String,Object>> learnerProfile(String school,String session,int sectionId,String term,String attributeId) {
        return safe("""
          SELECT s.stu_id AS studentId,s.registration_no AS registrationNo,
           UPPER(REPLACE(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,''),'  ',' ')) AS studentName,
           COALESCE(r.ATTRIBUTE_REMARK,'') AS remark,COALESCE(r.Attribute_Value_Id,'') AS attributeValueIds
          FROM TB_StudentMaster s JOIN student_other_info so ON so.school_id=s.school_id AND so.registration_no=s.registration_no
          LEFT JOIN TB_LERNERPROFILE_ATTRIBUTE_REMARKS r ON r.SCHOOL_ID=s.school_id AND r.SESSION=s.session AND r.STU_ID=s.stu_id AND r.ATTRIBUTE_ID=? AND r.TERM_ID=?
          WHERE s.school_id=? AND s.session=? AND s.sec_id=? AND LOWER(s.status)='registered' ORDER BY studentName
          """,attributeId,term,school,session,sectionId);
    }
    public void saveLearnerProfile(String school,String by,Map<String,Object> body) {
        String session=text(body.get("session")),term=text(body.get("termId")),attribute=text(body.get("attributeId"));int sec=integer(body.get("sectionId"));
        for(Map<String,Object> r:rows(body)){int stu=integer(r.get("studentId"));if(stu<=0)continue;String remark=text(r.get("remark")),ids=text(r.get("attributeValueIds"));int n=scalarInt("SELECT COUNT(*) FROM TB_LERNERPROFILE_ATTRIBUTE_REMARKS WHERE school_id=? AND session=? AND stu_id=? AND ATTRIBUTE_ID=? AND TERM_ID=?",school,session,stu,attribute,term);if(n>0)jdbc.update("UPDATE TB_LERNERPROFILE_ATTRIBUTE_REMARKS SET ATTRIBUTE_REMARK=?,Attribute_Value_Id=?,SEC_ID=?,CREATED_BY=? WHERE school_id=? AND session=? AND stu_id=? AND ATTRIBUTE_ID=? AND TERM_ID=?",remark,ids,sec,by,school,session,stu,attribute,term);else jdbc.update("INSERT INTO TB_LERNERPROFILE_ATTRIBUTE_REMARKS(stu_id,ATTRIBUTE_ID,ATTRIBUTE_REMARK,Attribute_Value_Id,term_id,session,school_id,SEC_ID,STATUS,CREATED_BY,CREATED_DATE) VALUES(?,?,?,?,?,?,?,?,?,?,GETDATE())",stu,attribute,remark,ids,term,session,school,sec,"active",by);}
    }

    public List<Map<String,Object>> remarks(String school,String session,int sectionId,String term,String remarkType,String examType) {
        return safe("""
          SELECT s.stu_id AS studentId,s.registration_no AS registrationNo,
           UPPER(REPLACE(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,''),'  ',' ')) AS studentName,
           COALESCE(r.remark,'') AS remark,COALESCE(r.total_Present,'') AS totalPresent,COALESCE(r.total_Days,'') AS totalDays
          FROM TB_StudentMaster s JOIN student_other_info so ON so.school_id=s.school_id AND so.registration_no=s.registration_no
          LEFT JOIN stu_remark r ON r.school_id=s.school_id AND r.session=s.session AND r.stu_id=s.stu_id AND r.term_id=? AND r.remark_type=? AND (r.exam_type_id=? OR ?='')
          WHERE s.school_id=? AND s.session=? AND s.sec_id=? AND LOWER(s.status)='registered' ORDER BY studentName
          """,term,remarkType,examType,examType,school,session,sectionId);
    }
    public void saveRemarks(String school,String by,Map<String,Object> body) {
        String session=text(body.get("session")),term=text(body.get("termId")),type=text(body.get("remarkType")),exam=text(body.get("examTypeId"));int sec=integer(body.get("sectionId"));
        for(Map<String,Object> r:rows(body)){int stu=integer(r.get("studentId"));if(stu<=0)continue;int n=scalarInt("SELECT COUNT(*) FROM stu_remark WHERE school_id=? AND session=? AND stu_id=? AND term_id=? AND remark_type=? AND (exam_type_id=? OR ?='')",school,session,stu,term,type,exam,exam);if(n>0)jdbc.update("UPDATE stu_remark SET remark=?,total_Present=?,total_Days=?,sec_id=?,created_by=?,created_date=GETDATE() WHERE school_id=? AND session=? AND stu_id=? AND term_id=? AND remark_type=? AND (exam_type_id=? OR ?='')",text(r.get("remark")).toUpperCase(),nullable(r.get("totalPresent")),nullable(r.get("totalDays")),sec,by,school,session,stu,term,type,exam,exam);else jdbc.update("INSERT INTO stu_remark(stu_id,sec_id,remark_type,remark,term_id,session,school_id,created_by,created_date,total_Present,total_Days,exam_type_id) VALUES(?,?,?,?,?,?,?, ?,GETDATE(),?,?,?)",stu,sec,type,text(r.get("remark")).toUpperCase(),term,session,school,by,nullable(r.get("totalPresent")),nullable(r.get("totalDays")),nullable(exam));}
    }

    public List<Map<String,Object>> reportcardRemarks(String school,String session,int sectionId,String term,String category,String subcategory) {
        return safe("""
          SELECT s.stu_id AS studentId,s.registration_no AS registrationNo,
           UPPER(REPLACE(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,''),'  ',' ')) AS studentName,
           COALESCE(r.Remark,'') AS remark
          FROM TB_StudentMaster s JOIN student_other_info so ON so.school_id=s.school_id AND so.registration_no=s.registration_no
          LEFT JOIN ReportCardSubCategoryRemark r ON r.school_id=s.school_id AND r.session=s.session AND r.stu_id=s.stu_id AND r.CategoryId=? AND r.SubCategoryId=? AND r.Term_id=?
          WHERE s.school_id=? AND s.session=? AND s.sec_id=? AND LOWER(s.status)='registered' ORDER BY studentName
          """,category,subcategory,term,school,session,sectionId);
    }
    public void saveReportcardRemarks(String school,String by,Map<String,Object> body) {
        String session=text(body.get("session")),term=text(body.get("termId")),cat=text(body.get("categoryId")),sub=text(body.get("subcategoryId"));int sec=integer(body.get("sectionId"));
        for(Map<String,Object> r:rows(body)){int stu=integer(r.get("studentId"));if(stu<=0)continue;int n=scalarInt("SELECT COUNT(*) FROM ReportCardSubCategoryRemark WHERE school_id=? AND session=? AND stu_id=? AND CategoryId=? AND SubCategoryId=? AND Term_id=?",school,session,stu,cat,sub,term);if(n>0)jdbc.update("UPDATE ReportCardSubCategoryRemark SET Remark=?,created_by=?,created_date=GETDATE() WHERE school_id=? AND session=? AND stu_id=? AND CategoryId=? AND SubCategoryId=? AND Term_id=?",text(r.get("remark")),by,school,session,stu,cat,sub,term);else jdbc.update("INSERT INTO ReportCardSubCategoryRemark(CategoryId,stu_id,Remark,created_date,created_by,Term_id,session,school_id,SubCategoryId) VALUES(?,?,?,GETDATE(),?,?,?,?,?)",cat,stu,text(r.get("remark")),by,term,session,school,sub);}
    }

    public List<Map<String,Object>> greenSheet(String school,String session,int sectionId) {
        return safe("SELECT Row_Id,STU_ID,stu_NAME,CLASS_ID,sec_id,SUB_ID,sub_NAME,PA1,PA2,PT1,NB1,SE1,HY_TH,HY_PR,PA3,PA4,PT2,NB2,SE2,Y_TH,Y_PR FROM exam_reports WHERE school_id=? AND session=? AND sec_id=? ORDER BY stu_NAME", school,session,sectionId);
    }

    /** Reads the same row-oriented workbook used by Upload-marks without a procedure call. */
    public int uploadMarks(String school, String by, String session, int sectionId, MultipartFile file) throws Exception {
        String filename = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();
        if (filename.endsWith(".txt") || filename.endsWith(".dat")) return uploadGazette(school, by, session, sectionId, file);
        int count = 0;
        DataFormatter formatter = new DataFormatter();
        try (InputStream in = file.getInputStream(); Workbook workbook = WorkbookFactory.create(in)) {
            Sheet sheet = workbook.getSheetAt(0); Row header = sheet.getRow(0);
            if (header == null) throw new IllegalArgumentException("Excel header row is missing");
            Map<String,Integer> columns = new LinkedHashMap<>();
            for (Cell cell : header) columns.put(formatter.formatCellValue(cell).trim().toLowerCase().replace("_", " "), cell.getColumnIndex());
            for (int i=1;i<=sheet.getLastRowNum();i++) {
                Row row=sheet.getRow(i); if(row==null) continue;
                String registration=cell(row,columns,formatter,"registration no","admission no","registration");
                String stuId=cell(row,columns,formatter,"stu id","student id");
                int student=toInt(stuId);
                if(student<=0 && !registration.isBlank()) student=scalarInt("SELECT TOP 1 stu_id FROM TB_StudentMaster WHERE school_id=? AND session=? AND sec_id=? AND registration_no=?",school,session,sectionId,registration);
                String subject=cell(row,columns,formatter,"subject id","subject");
                String exam=cell(row,columns,formatter,"exam type id","exam type","exam");
                String term=cell(row,columns,formatter,"term id","term");
                String marks=cell(row,columns,formatter,"marks","grade");
                if(student<=0 || subject.isBlank() || exam.isBlank() || term.isBlank()) continue;
                String absent=cell(row,columns,formatter,"absentism","absent");
                int exists=scalarInt("SELECT COUNT(*) FROM marks_entry WHERE school_id=? AND Stu_id=? AND Session=? AND sec_id=? AND Subject_id=? AND term_id=? AND exam_type_id=?",school,student,session,sectionId,subject,term,exam);
                if(exists>0) jdbc.update("UPDATE marks_entry SET Marks=?,Absentism=?,modified_date=GETDATE(),modified_by=? WHERE school_id=? AND Stu_id=? AND Session=? AND sec_id=? AND Subject_id=? AND term_id=? AND exam_type_id=?",nullable(marks),absent,by,school,student,session,sectionId,subject,term,exam);
                else jdbc.update("INSERT INTO marks_entry(Stu_id,sec_id,class_id,Subject_id,exam_type_id,term_id,Marks,Absentism,created_date,created_by,school_id,Session) SELECT ?,sec_id,class_id,?,?,?,?,GETDATE(),?,?,? FROM TB_Class WHERE school_id=? AND sec_id=?",student,subject,exam,term,nullable(marks),absent,by,school,school,sectionId);
                count++;
            }
        }
        return count;
    }

    public List<Map<String,Object>> uploadResults(String school,String session,int sectionId) {
        int classId=scalarInt("SELECT TOP 1 class_id FROM TB_Class WHERE school_id=? AND sec_id=?",school,sectionId);
        return safe("""
            WITH cte AS (
              SELECT s.rollno,s.class,s.session,t.Marks,100 AS max_marks,
                     ROW_NUMBER() OVER (PARTITION BY s.rollno ORDER BY t.Marks DESC) AS rankno
              FROM StudentExamResults s
              CROSS APPLY (VALUES
                (s.Subject1_Code,TRY_CAST(s.Marks1 AS float)),(s.Subject2_Code,TRY_CAST(s.Marks2 AS float)),
                (s.Subject3_Code,TRY_CAST(s.Marks3 AS float)),(s.Subject4_Code,TRY_CAST(s.Marks4 AS float)),
                (s.Subject5_Code,TRY_CAST(s.Marks5 AS float)),(s.Subject6_Code,TRY_CAST(s.Marks6 AS float)),
                (s.Subject7_Code,TRY_CAST(s.Marks7 AS float))) t(SubjectCode,Marks)
              WHERE t.SubjectCode<>'0' AND s.class=? AND s.session=? AND s.school_id=?
            ), final_cte AS (
              SELECT class,session,rollno,
                SUM(CASE WHEN rankno<=5 THEN Marks ELSE 0 END) best5Total,
                SUM(CASE WHEN rankno<=5 THEN max_marks ELSE 0 END) best5Max,
                ROUND(SUM(CASE WHEN rankno<=5 THEN Marks ELSE 0 END)*100.0/NULLIF(SUM(CASE WHEN rankno<=5 THEN max_marks ELSE 0 END),0),2) best5Pct,
                SUM(Marks) totalMarks,SUM(max_marks) totalMax,
                ROUND(SUM(Marks)*100.0/NULLIF(SUM(max_marks),0),2) overallPct
              FROM cte GROUP BY rollno,class,session
            )
            SELECT ss.RollNo AS rollNo,ss.Gender AS gender,ss.StudentName AS studentName,ss.class AS classId,ss.session AS session,ss.Result AS result,
              ss.Subject1_Code AS subject1Code,ss.Marks1 AS marks1,ss.Grade1 AS grade1,ss.Subject2_Code AS subject2Code,ss.Marks2 AS marks2,ss.Grade2 AS grade2,
              ss.Subject3_Code AS subject3Code,ss.Marks3 AS marks3,ss.Grade3 AS grade3,ss.Subject4_Code AS subject4Code,ss.Marks4 AS marks4,ss.Grade4 AS grade4,
              ss.Subject5_Code AS subject5Code,ss.Marks5 AS marks5,ss.Grade5 AS grade5,ss.Subject6_Code AS subject6Code,ss.Marks6 AS marks6,ss.Grade6 AS grade6,
              ss.Subject7_Code AS subject7Code,ss.Marks7 AS marks7,ss.Grade7 AS grade7,ss.CompartmentSubject AS compartmentSubject,
              ss.INT_SB_GRD1 AS internalGrade1,ss.INT_SB_GRD2 AS internalGrade2,ss.INT_SB_GRD3 AS internalGrade3,
              CONCAT(CONVERT(varchar(40),fc.best5Total),'/',CONVERT(varchar(40),fc.best5Max)) AS best5Total,
              CONVERT(varchar(40),CONVERT(decimal(10,2),fc.best5Pct)) AS best5Percentage,
              CONCAT(CONVERT(varchar(40),fc.totalMarks),'/',CONVERT(varchar(40),fc.totalMax)) AS totalMarks,
              CONVERT(decimal(10,2),fc.overallPct) AS overallPercentage
            FROM StudentExamResults ss JOIN final_cte fc ON ss.class=fc.class AND ss.session=fc.session AND ss.RollNo=fc.RollNo
            WHERE ss.school_id=? AND ss.class=? AND ss.session=?
            ORDER BY ss.RollNo
            """, classId, session, school, school, classId, session);
    }

    /** Direct-SQL equivalent of the legacy CBSE gazette upload. It preserves the
     * fixed-width parser and upserts StudentExamResults without executing either
     * legacy upload procedure. */
    private int uploadGazette(String school, String by, String session, int sectionId, MultipartFile file) throws Exception {
        int classId = scalarInt("SELECT TOP 1 class_id FROM TB_Class WHERE school_id=? AND sec_id=?", school, sectionId);
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            String uid = java.util.UUID.randomUUID().toString();
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isBlank() || trimmed.contains("MKS GR MKS GR MKS GR") || trimmed.contains("SUB CD SUB")
                    || trimmed.contains("ROLL") || trimmed.contains("SCHOOL") || trimmed.contains("SUBJECTS")
                    || trimmed.contains("PAGE") || trimmed.startsWith("-") || line.length() < 50) continue;
                try {
                    String[] first = Pattern.compile("\\s+").split(trimmed);
                    if (first.length < 2) continue;
                    long rollNo = Long.parseLong(first[0]);
                    int index = 1; String rollFlag = "";
                    if (index < first.length && ("I".equals(first[index]) || "C".equals(first[index]))) { rollFlag = first[index++]; }
                    if (index >= first.length) continue;
                    String gender = first[index];
                    String name = line.length() >= 58 ? line.substring(13, Math.min(58, line.length())).trim() : "";
                    String line1 = line.length() > 64 ? line.substring(64, Math.min(line.length(), 111)).trim() : "";
                    String line2 = line.length() > 111 ? line.substring(111).trim() : "";
                    String[] subjects = line1.isBlank() ? new String[0] : Pattern.compile("\\s+").split(line1);
                    String[] resultParts = line2.isBlank() ? new String[0] : Pattern.compile("\\s+").split(line2);
                    String[] subject = new String[7]; for (int i=0;i<subject.length;i++) subject[i] = i < subjects.length ? subjects[i] : "";
                    if (classId == 10) subject[6] = subjects.length > 5 ? subjects[5] : "0";
                    String result = resultParts.length > 0 ? resultParts[0] : "";
                    String compartment = resultParts.length > 1 ? resultParts[1] : "";
                    String marksLine = reader.readLine(); if (marksLine == null || marksLine.trim().isBlank()) continue;
                    String[] markParts = Pattern.compile("\\s+").split(marksLine.trim());
                    String[] marks = new String[7], grades = new String[7];
                    for (int i=0;i<7;i++) { marks[i] = i*2 < markParts.length ? markParts[i*2] : ""; grades[i] = i*2+1 < markParts.length ? markParts[i*2+1] : ""; }
                    String int1 = "", int2 = "", int3 = "";
                    if (classId != 10) { int1 = resultParts.length > 0 ? resultParts[0] : ""; int2 = resultParts.length > 1 ? resultParts[1] : ""; int3 = resultParts.length > 2 ? resultParts[2] : ""; result = resultParts.length > 3 ? resultParts[3] : ""; compartment = resultParts.length > 4 ? resultParts[4] : ""; }
                    upsertExamResult(school, by, uid, classId, session, rollNo, gender, name, subject, compartment, marks, grades, result, int1, int2, int3, rollFlag);
                    count++;
                } catch (RuntimeException ignored) { /* same row-level tolerance as the legacy page */ }
            }
        }
        updateStreams(school, classId, session);
        return count;
    }

    private void upsertExamResult(String school,String by,String uid,int classId,String session,long rollNo,String gender,String name,String[] subject,String compartment,String[] marks,String[] grades,String result,String int1,String int2,String int3,String rollFlag) {
        int exists=scalarInt("SELECT COUNT(*) FROM StudentExamResults WHERE RollNo=? AND StudentName=? AND class=? AND school_id=? AND session=?",rollNo,name,classId,school,session);
        if (exists>0) jdbc.update("""
            UPDATE StudentExamResults SET Gender=?,Subject1_Code=?,Subject2_Code=?,Subject3_Code=?,Subject4_Code=?,Subject5_Code=?,Subject6_Code=?,Subject7_Code=?,CompartmentSubject=?,
              Marks1=?,Grade1=?,Marks2=?,Grade2=?,Marks3=?,Grade3=?,Marks4=?,Grade4=?,Marks5=?,Grade5=?,Marks6=?,Grade6=?,Marks7=?,Grade7=?,Result=?,uid=?,INT_SB_GRD1=?,INT_SB_GRD2=?,INT_SB_GRD3=?,rollFlag=?
            WHERE RollNo=? AND StudentName=? AND class=? AND school_id=? AND session=?
            """,gender,subject[0],subject[1],subject[2],subject[3],subject[4],subject[5],subject[6],compartment,marks[0],grades[0],marks[1],grades[1],marks[2],grades[2],marks[3],grades[3],marks[4],grades[4],marks[5],grades[5],marks[6],grades[6],result,uid,int1,int2,int3,rollFlag,rollNo,name,classId,school,session);
        else jdbc.update("""
            INSERT INTO StudentExamResults(RollNo,Gender,StudentName,Subject1_Code,Subject2_Code,Subject3_Code,Subject4_Code,Subject5_Code,Subject6_Code,Subject7_Code,CompartmentSubject,
              Marks1,Grade1,Marks2,Grade2,Marks3,Grade3,Marks4,Grade4,Marks5,Grade5,Marks6,Grade6,Marks7,Grade7,Result,class,session,uid,INT_SB_GRD1,INT_SB_GRD2,INT_SB_GRD3,school_id,rollFlag)
            VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
            """,rollNo,gender,name,subject[0],subject[1],subject[2],subject[3],subject[4],subject[5],subject[6],compartment,marks[0],grades[0],marks[1],grades[1],marks[2],grades[2],marks[3],grades[3],marks[4],grades[4],marks[5],grades[5],marks[6],grades[6],result,classId,session,uid,int1,int2,int3,school,rollFlag);
    }

    private void updateStreams(String school,int classId,String session) {
        String base="school_id=? AND class=? AND session=? AND ISNULL(Stream,'')='' AND Marks1<>'AB' AND Marks2<>'AB' AND Marks3<>'AB' AND Marks4<>'AB' AND Marks5<>'AB' AND Marks6<>'AB' AND Marks7<>'AB'";
        jdbc.update("UPDATE StudentExamResults SET Stream='Science' WHERE "+base+" AND EXISTS (SELECT 1 FROM dpssubjectwithcode d WHERE d.school_id=? AND d.SubjectName='Physics' AND d.SubjectCode IN (Subject1_Code,Subject2_Code,Subject3_Code,Subject4_Code,Subject5_Code,Subject6_Code,Subject7_Code))",school,classId,session,school);
        jdbc.update("UPDATE StudentExamResults SET Stream='Commerce' WHERE "+base+" AND EXISTS (SELECT 1 FROM dpssubjectwithcode d WHERE d.school_id=? AND d.SubjectName='Accountancy' AND d.SubjectCode IN (Subject1_Code,Subject2_Code,Subject3_Code,Subject4_Code,Subject5_Code,Subject6_Code,Subject7_Code))",school,classId,session,school);
        jdbc.update("UPDATE StudentExamResults SET Stream='Humanities' WHERE "+base,school,classId,session);
    }

    private static String cell(Row row, Map<String,Integer> columns, DataFormatter formatter, String... names) {
        for(String name:names){Integer index=columns.get(name);if(index!=null){Cell c=row.getCell(index);return c==null?"":formatter.formatCellValue(c).trim();}}
        return "";
    }
    private static int toInt(String value){try{return Integer.parseInt(value.trim());}catch(Exception e){return 0;}}

    private List<Map<String,Object>> safe(String sql,Object... args){try{return jdbc.queryForList(sql,args);}catch(DataAccessException e){return List.of();}}
    private int scalarInt(String sql,Object... args){try{Integer n=jdbc.queryForObject(sql,Integer.class,args);return n==null?0:n;}catch(DataAccessException e){return 0;}}
    private String scalarText(String sql,Object... args){try{Object n=jdbc.queryForObject(sql,Object.class,args);return n==null?"":String.valueOf(n);}catch(DataAccessException e){return "";}}
    private static String weightedMarks(String marks,String max,String weightage,String absent){
        if (absent != null && !absent.isBlank()) {
            if ("AB".equalsIgnoreCase(absent)) return "AB";
            if ("ML".equalsIgnoreCase(absent) || "NA".equalsIgnoreCase(absent)) return "0";
        }
        try {
            if (marks.isBlank() || max.isBlank() || weightage.isBlank()) return marks;
            return String.valueOf(Math.round(Double.parseDouble(marks) * Double.parseDouble(weightage) / Double.parseDouble(max) * 100.0) / 100.0);
        } catch (Exception e) { return marks; }
    }
    private String primaryGrade(String school,String session,String marks,String max,Object classGroup,Object classType,boolean ceiling){
        try {
            double denominator=Double.parseDouble(max);
            if(denominator==0) denominator=1;
            double score=Double.parseDouble(marks)*100.0/denominator;
            if(ceiling) score=Math.ceil(score);
            List<Map<String,Object>> r=safe("""
                SELECT TOP 1 grade FROM chk_grade_primary
                WHERE school_id=? AND (?='' OR class_group=?) AND (ClassType=? OR ClassType IS NULL)
                  AND TRY_CONVERT(decimal(18,2),from_range)<=? AND TRY_CONVERT(decimal(18,2),to_range)>=?
                ORDER BY GradeOrder
                """,school,text(classGroup),text(classGroup),text(classType),score,score);
            return r.isEmpty()?"":text(r.get(0).get("grade"));
        } catch(Exception e){return "";}
    }
    private static String text(Object v){return v==null?"":String.valueOf(v);}
    private static int integer(Object v){try{return Integer.parseInt(text(v));}catch(Exception e){return 0;}}
    private static Object nullable(Object v){String s=text(v);return s.isBlank()?null:s;}
    @SuppressWarnings("unchecked") private static List<Map<String,Object>> rows(Map<String,Object> b){Object v=b.get("rows");return v instanceof List<?> l?(List<Map<String,Object>>)(List<?>)l:List.of();}
}
