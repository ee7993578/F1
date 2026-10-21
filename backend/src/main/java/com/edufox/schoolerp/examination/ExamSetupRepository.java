package com.edufox.schoolerp.examination;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Direct table implementation of the Examination/Setup pages.
 *
 * The old ASP.NET pages call procedures.  This repository intentionally keeps
 * the same table-level behaviour without executing those procedures.
 */
@Repository
@RequiredArgsConstructor
public class ExamSetupRepository {
    private final JdbcTemplate jdbc;

    public Map<String, Object> bootstrap(String school, String staffUid) {
        String session = defaultSession(school);
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("session", session);
        out.put("sessions", options(school, "sessions", Map.of("session", session)));
        out.put("classes", options(school, "classes", Map.of("session", session)));
        out.put("subjects", options(school, "subjects", Map.of("session", session)));
        out.put("classTypes", List.of(Map.of("id", "Regular", "name", "Regular"), Map.of("id", "Cambridge", "name", "Cambridge")));
        out.put("indicatorCategories", options(school, "indicator-categories", Map.of("session", session)));
        out.put("learnerAttributes", options(school, "learner-attributes", Map.of("session", session)));
        out.put("defaultLock", defaultLock(school));
        return out;
    }

    public List<Map<String, Object>> options(String school, String type, Map<String, String> p) {
        String session = value(p, "session", defaultSession(school));
        String classType = value(p, "classType", "Regular");
        String classId = value(p, "classId", "");
        String sectionId = value(p, "sectionId", "");
        String headerId = value(p, "headerId", "");
        String categoryId = value(p, "categoryId", "");
        String termId = value(p, "termId", "");

        return switch (type == null ? "" : type.toLowerCase()) {
            case "sessions" -> safe("SELECT session AS id,session AS name FROM TB_Session WHERE school_id=? ORDER BY id DESC", school);
            case "classes" -> safe("SELECT id,class_name AS name,class_group AS classGroup FROM class_name WHERE school_id=? ORDER BY COALESCE(class_order,id),id", school);
            // The legacy class/marks pages only show sections for classes 17/18.
            // Define Practical Subject has a separate all-sections binding below.
            case "sections" -> ("17".equals(classId) || "18".equals(classId))
                    ? safe("SELECT sec_id AS id,section AS name,class_id AS classId,ClassType AS classType FROM TB_Class WHERE school_id=? AND class_id=? ORDER BY sec_id", school, classId)
                    : List.of();
            case "all-sections" -> safe("SELECT c.sec_id AS id,cn.class_name+'('+c.section+')' AS name,c.class_id AS classId,c.ClassType AS classType FROM TB_Class c JOIN class_name cn ON cn.school_id=c.school_id AND cn.id=c.class_id WHERE c.school_id=? AND c.class_id IN (SELECT id FROM class_name WHERE school_id=?) ORDER BY c.class_id,c.sec_id", school, school);
            case "practical-theory-subjects" -> practicalTheorySubjects(school, session, sectionId);
            case "practical-subjects" -> practicalSubjectOptions(school, session, sectionId);
            case "terms" -> terms(school, session, classId);
            case "subjects" -> safe("SELECT id,Subject AS name,Subject_code AS code,Practical AS practical FROM TB_SubjectMaster WHERE school_id=? AND (status IS NULL OR status=1) ORDER BY Subject", school);
            case "class-subjects" -> safe("""
                SELECT DISTINCT cs.subject_id AS id,sm.Subject AS name,sm.Subject_code AS code,
                    sm.Practical AS practical,cs.isoptional AS optional,cs.isgrade AS grade,
                    cs.subject_order AS displayOrder
                FROM TB_ClassSubject cs JOIN TB_SubjectMaster sm ON sm.school_id=cs.school_id AND sm.id=cs.subject_id
                WHERE cs.school_id=? AND cs.session=? AND cs.class_id=? AND (sm.status IS NULL OR sm.status=1)
                ORDER BY COALESCE(cs.subject_order,9999),sm.Subject
                """, school, session, classId);
            case "sub-subjects" -> safe("SELECT id,Subject AS name,Subject_code AS code,Practical AS practical FROM TB_SubjectMaster WHERE school_id=? AND (status IS NULL OR status=1) AND id<>? ORDER BY Subject", school, valueInt(p, "subjectId", 0));
            case "headers" -> safe("SELECT header_id AS id,header_name AS name,header_order AS displayOrder,ClassType AS classType FROM header_name WHERE school_id=? AND session=? AND ClassType=? AND (status IS NULL OR status=1) ORDER BY TRY_CONVERT(int,header_order),header_id", school, session, classType);
            case "categories" -> safe("SELECT category_id AS id,category_name AS name,header_id AS headerId,cat_order AS displayOrder,ClassType AS classType FROM category_name WHERE school_id=? AND session=? AND class_id=? AND header_id=? AND ClassType=? AND (status IS NULL OR status=1) ORDER BY COALESCE(cat_order,9999),category_id", school, session, classId, headerId, classType);
            case "attributes" -> safe("SELECT attribute_id AS id,attribute_name AS name,header_id AS headerId,category_id AS categoryId,attribute_order AS displayOrder,ClassType AS classType FROM attribute_name WHERE school_id=? AND session=? AND class_id=? AND header_id=? AND category_id=? AND ClassType=? AND status=1 ORDER BY COALESCE(attribute_order,9999),attribute_id", school, session, classId, headerId, categoryId, classType);
            case "exam-types" -> examTypes(school, session, classId, termId);
            case "learner-attributes" -> safe("SELECT ATTRIBUTE_ID AS id,ATTRIBUTE_NAME AS name,HasAttributeValue AS hasValues FROM TB_LERNERPROFILE_ATTRIBUTE WHERE SCHOOL_ID=? AND SESSION=? AND STATUS='active' ORDER BY COALESCE(ATTR_ORDER,ATTRIBUTE_ID)", school, session);
            case "learner-values" -> safe("SELECT id,Attribute_Value_Id AS valueId,Attribute_Value AS name FROM LERNERPROFILE_ATTRIBUTE_Value WHERE school_id=? AND session=? AND ATTRIBUTE_ID=? AND status=1 ORDER BY Attribute_Value", school, session, value(p, "attributeId", ""));
            case "indicator-categories" -> safe("SELECT id,indicator_category AS name,ClassType AS classType,indicatorCATOrder AS displayOrder FROM tb_indicatorCAT WHERE school_id=? AND status='active' ORDER BY COALESCE(indicatorCATOrder,id)", school);
            case "indicators" -> safe("SELECT ID AS id,indicator_ID AS code,indicator_Name AS name,indicatorOrder AS displayOrder,class_id AS classId,indicator_category_id AS categoryId,indicator_cat AS categoryName FROM TB_INDICATOR WHERE school_id=? AND session=? AND status='active' ORDER BY class_id,COALESCE(IndicatorOrder,ID)", school, session);
            case "lock-classes" -> lockClasses(school, session);
            case "lock-subjects" -> lockSubjects(school, session, sectionId, value(p, "marksType", "Marks"));
            case "lock-exam-types" -> examTypes(school, session, classId, termId);
            default -> List.of();
        };
    }

    public Map<String, Object> data(String school, String page, Map<String, String> p) {
        String key = page == null ? "" : page.toLowerCase();
        String session = value(p, "session", defaultSession(school));
        Map<String, Object> out = new LinkedHashMap<>();
        switch (key) {
            case "subjects" -> out.put("rows", subjects(school));
            case "class-subjects" -> out.put("rows", classSubjects(school, session, p));
            case "sub-subjects" -> out.put("rows", subSubjects(school, session, p));
            case "practical-subjects" -> out.put("rows", practicalSubjects(school, session, p));
            case "headers" -> out.put("rows", options(school, "headers", p));
            case "categories" -> out.put("rows", categories(school, session, p));
            case "attributes" -> out.put("rows", attributes(school, session, p));
            case "weightage" -> out.put("rows", weightages(school, session, p));
            case "max-min" -> out.put("rows", maxMin(school, session, p));
            case "primary-max-min" -> out.put("rows", primaryMaxMin(school, session, p));
            case "learner-values" -> out.put("rows", learnerValues(school, session, p));
            case "indicator-categories" -> out.put("rows", options(school, "indicator-categories", p));
            case "indicators" -> out.put("rows", options(school, "indicators", p));
            case "chart-lock" -> out.put("rows", chartLock(school, session));
            case "exam-lock" -> {
                out.put("defaultLock", defaultLock(school));
                out.put("rows", examLock(school, session, p));
            }
            default -> out.put("rows", List.of());
        }
        return out;
    }

    public void save(String school, String staff, String page, Map<String, Object> body) {
        String key = page == null ? "" : page.toLowerCase();
        switch (key) {
            case "subjects" -> saveSubject(school, staff, body);
            case "class-subjects" -> saveClassSubjects(school, staff, body);
            case "sub-subjects" -> saveSubSubjects(school, staff, body);
            case "practical-subjects" -> savePracticalSubject(school, staff, body);
            case "headers" -> saveHeader(school, staff, body);
            case "categories" -> saveCategory(school, staff, body);
            case "attributes" -> saveAttribute(school, staff, body);
            case "weightage" -> saveWeightages(school, staff, body);
            case "max-min" -> saveMaxMin(school, staff, body);
            case "primary-max-min" -> savePrimaryMaxMin(school, staff, body);
            case "learner-values" -> saveLearnerValue(school, staff, body);
            case "indicator-categories" -> saveIndicatorCategory(school, body);
            case "indicators" -> saveIndicator(school, staff, body);
            case "chart-lock" -> saveChartLock(school, staff, body);
            case "exam-lock" -> saveExamLock(school, staff, body);
            default -> throw new IllegalArgumentException("Unknown examination setup page: " + page);
        }
    }

    public void delete(String school, String page, Map<String, Object> body) {
        String key = page == null ? "" : page.toLowerCase();
        if ("indicator-categories".equals(key)) {
            jdbc.update("UPDATE tb_indicatorCAT SET status='notactive' WHERE school_id=? AND id=?", school, integer(body.get("id")));
        } else if ("indicators".equals(key)) {
            jdbc.update("UPDATE TB_INDICATOR SET status='inactive' WHERE school_id=? AND ID=?", school, integer(body.get("id")));
        } else if ("practical-subjects".equals(key)) {
            jdbc.update("DELETE FROM TB_Pract_Subject WHERE school_id=? AND session=? AND sec_id=? AND subject_id=?", school, text(body.get("session")), integer(body.get("sectionId")), integer(body.get("subjectId")));
        } else {
            throw new IllegalArgumentException("Delete is not available for this setup page");
        }
    }

    private List<Map<String, Object>> subjects(String school) {
        return safe("SELECT id,Subject AS name,Subject_code AS code,Practical AS practical FROM TB_SubjectMaster WHERE school_id=? AND (status IS NULL OR status=1) ORDER BY Subject", school);
    }

    private List<Map<String, Object>> classSubjects(String school, String session, Map<String, String> p) {
        int classId = valueInt(p, "classId", 0), sectionId = valueInt(p, "sectionId", 0);
        int stored = sectionId > 0 ? sectionId : classId;
        return safe("""
            SELECT sm.id,sm.Subject AS name,sm.Subject_code AS code,sm.Practical AS practical,
              COALESCE(cs.status,0) AS selected,COALESCE(cs.isoptional,0) AS optional,
              COALESCE(cs.isgrade,0) AS grade,COALESCE(cs.subject_order,0) AS displayOrder
            FROM TB_SubjectMaster sm LEFT JOIN TB_ClassSubject cs ON cs.school_id=sm.school_id AND cs.subject_id=sm.id
              AND cs.class_id=? AND cs.session=?
            WHERE sm.school_id=? AND (sm.status IS NULL OR sm.status=1)
            ORDER BY sm.Subject
            """, stored, session, school);
    }

    private List<Map<String, Object>> subSubjects(String school, String session, Map<String, String> p) {
        int classId = valueInt(p, "classId", 0), subjectId = valueInt(p, "subjectId", 0);
        return safe("""
            SELECT sm.id,sm.Subject AS name,sm.Subject_code AS code,
              CASE WHEN ss.id IS NULL THEN 0 ELSE 1 END AS selected
            FROM TB_SubjectMaster sm LEFT JOIN SubSubject ss ON ss.school_id=sm.school_id AND ss.session=?
              AND ss.class_id=? AND ss.subject_id=? AND ss.sub_subject_id=sm.id
            WHERE sm.school_id=? AND sm.id<>? AND (sm.status IS NULL OR sm.status=1)
            ORDER BY sm.Subject
            """, session, classId, subjectId, school, subjectId);
    }

    private List<Map<String, Object>> practicalSubjects(String school, String session, Map<String, String> p) {
        return safe("""
            SELECT ps.subject_id AS subjectId,sm.Subject AS subjectName,ps.pr_subject_id AS practicalSubjectId,
              pm.Subject AS practicalName
            FROM TB_Pract_Subject ps
            JOIN TB_SubjectMaster sm ON sm.school_id=ps.school_id AND sm.id=ps.subject_id
            JOIN TB_SubjectMaster pm ON pm.school_id=ps.school_id AND pm.id=ps.pr_subject_id
            WHERE ps.school_id=? AND ps.session=? AND ps.sec_id=?
            ORDER BY sm.Subject,pm.Subject
            """, school, session, value(p, "sectionId", "0"));
    }

    private List<Map<String, Object>> practicalTheorySubjects(String school, String session, String sectionId) {
        int stored = practicalStoredClass(school, sectionId);
        return safe("""
            SELECT DISTINCT cs.subject_id AS id,sm.Subject AS name,sm.Subject_code AS code,sm.Practical AS practical
            FROM TB_ClassSubject cs JOIN TB_SubjectMaster sm ON sm.school_id=cs.school_id AND sm.id=cs.subject_id
            WHERE cs.school_id=? AND cs.session=? AND cs.class_id=? AND cs.isprac='N' AND cs.status=1
              AND sm.status=1 AND sm.Practical='N'
            ORDER BY sm.Subject
            """, school, session, stored);
    }

    private List<Map<String, Object>> practicalSubjectOptions(String school, String session, String sectionId) {
        int stored = practicalStoredClass(school, sectionId);
        return safe("""
            SELECT DISTINCT sm.id,sm.Subject AS name,sm.Subject_code AS code,sm.Practical AS practical
            FROM TB_SubjectMaster sm JOIN TB_ClassSubject cs ON cs.school_id=sm.school_id AND cs.subject_id=sm.id
            WHERE sm.school_id=? AND sm.status=1 AND sm.Practical='Y' AND cs.session=? AND cs.class_id=? AND cs.status=1
            ORDER BY sm.Subject
            """, school, session, stored);
    }

    private int practicalStoredClass(String school, String sectionId) {
        int sec = integer(sectionId);
        int classId = scalarInt("SELECT TOP 1 class_id FROM TB_Class WHERE school_id=? AND sec_id=?", school, sec);
        return classId == 17 || classId == 18 ? sec : classId;
    }

    private List<Map<String, Object>> categories(String school, String session, Map<String, String> p) {
        return safe("SELECT category_id AS id,category_name AS name,header_id AS headerId,cat_order AS displayOrder,ClassType AS classType,class_id AS classId FROM category_name WHERE school_id=? AND session=? AND class_id=? AND ClassType=? ORDER BY COALESCE(cat_order,9999),category_id", school, session, value(p, "classId", "0"), value(p, "classType", "Regular"));
    }

    private List<Map<String, Object>> attributes(String school, String session, Map<String, String> p) {
        return safe("SELECT attribute_id AS id,attribute_name AS name,header_id AS headerId,category_id AS categoryId,attribute_order AS displayOrder,ClassType AS classType,class_id AS classId FROM attribute_name WHERE school_id=? AND session=? AND class_id=? AND ClassType=? ORDER BY category_id,COALESCE(attribute_order,9999),attribute_id", school, session, value(p, "classId", "0"), value(p, "classType", "Regular"));
    }

    private List<Map<String, Object>> weightages(String school, String session, Map<String, String> p) {
        int classId = valueInt(p, "classId", 0), termId = valueInt(p, "termId", 0);
        return safe("""
            SELECT e.exam_type_id AS examTypeId,e.exam_name_dis AS examName,et.exam_id AS examId,
              e.exam_order AS displayOrder,COALESCE(w.weightage,'') AS weightage
            FROM exam_set e JOIN exam_type et ON et.school_id=e.school_id AND et.exam_type_id=e.exam_type_id
            JOIN assign_exam_set a ON a.school_id=e.school_id AND a.exam_set_id=e.exam_set_id
            LEFT JOIN TB_WeightageMarks_new w ON w.school_id=e.school_id AND w.session=? AND w.class_id=?
              AND w.term=? AND w.exam_type_id=e.exam_type_id AND w.exam_id=et.exam_id
            WHERE e.school_id=? AND e.term_id=? AND a.class_id=?
              AND TRY_CONVERT(int,a.from_year)<=TRY_CONVERT(int,LEFT(?,4))
              AND (CONVERT(varchar(20),a.to_year)='0' OR TRY_CONVERT(int,a.to_year)>=TRY_CONVERT(int,RIGHT(?,4)))
            ORDER BY COALESCE(e.exam_order,9999),e.exam_type_id
            """, session, classId, termId, school, termId, classId, session, session);
    }

    private List<Map<String, Object>> maxMin(String school, String session, Map<String, String> p) {
        int classId = valueInt(p, "classId", 0), sectionId = valueInt(p, "sectionId", 0), termId = valueInt(p, "termId", 0);
        int stored = sectionId > 0 ? sectionId : classId;
        String lockMembership = sectionId > 0 ? "st.sec_id=?" : "st.class_id=?";
        String sql = """
            SELECT cs.subject_id AS subjectId,sm.Subject AS subjectName,sm.Subject_code AS subjectCode,
              e.exam_type_id AS examTypeId,e.exam_name_dis AS examName,et.exam_id AS examId,e.exam_order AS displayOrder,
              COALESCE(CONVERT(varchar(50),mm.max_marks),'') AS maxMarks,COALESCE(CONVERT(varchar(50),mm.min_marks),'') AS minMarks,
              COALESCE(CONVERT(varchar(30),mm.E_Date,106),'') AS examDate,COALESCE(mm.Grade_subject,'') AS gradeSubject,
              CASE WHEN EXISTS (
                SELECT 1 FROM senior_marks m
                WHERE m.school_id=cs.school_id AND m.Session=?
                  AND m.Subject_id=CONVERT(varchar(50),cs.subject_id)
                  AND m.Term_id=CONVERT(varchar(50),e.term_id)
                  AND m.Exam_type_id=CONVERT(varchar(50),e.exam_type_id)
                  AND EXISTS (
                    SELECT 1 FROM TB_StudentMaster st
                    WHERE st.school_id=m.school_id AND st.session=m.Session
                      AND CONVERT(varchar(50),st.stu_id)=m.Stu_id AND %s
                  )
              ) THEN 1 ELSE 0 END AS marksLocked
            FROM TB_ClassSubject cs JOIN TB_SubjectMaster sm ON sm.school_id=cs.school_id AND sm.id=cs.subject_id
            JOIN exam_set e ON e.school_id=cs.school_id AND e.term_id=?
            JOIN exam_type et ON et.school_id=e.school_id AND et.exam_type_id=e.exam_type_id
            JOIN assign_exam_set a ON a.school_id=e.school_id AND a.exam_set_id=e.exam_set_id AND a.class_id=?
            LEFT JOIN tb_exam_max_min_new mm ON mm.school_id=cs.school_id AND mm.Session=? AND mm.class_id=?
              AND mm.subject_id=cs.subject_id AND mm.Term_id=? AND mm.Exam_type_id=e.exam_type_id AND mm.Exam_id=et.exam_id
            WHERE cs.school_id=? AND cs.session=? AND cs.class_id=? AND cs.status=1
              AND TRY_CONVERT(int,a.from_year)<=TRY_CONVERT(int,LEFT(?,4))
              AND (CONVERT(varchar(20),a.to_year)='0' OR TRY_CONVERT(int,a.to_year)>=TRY_CONVERT(int,RIGHT(?,4)))
            ORDER BY cs.subject_id,COALESCE(e.exam_order,9999),e.exam_type_id
            """.formatted(lockMembership);
        return safe(sql, session, stored, termId, classId, session, stored, termId, school, session, stored, session, session);
    }

    private List<Map<String, Object>> primaryMaxMin(String school, String session, Map<String, String> p) {
        String classId = value(p, "classId", "0"), headerId = value(p, "headerId", "0"), categoryId = value(p, "categoryId", "0");
        return safe("""
            SELECT a.attribute_id AS attributeId,a.attribute_name AS attributeName,
              COALESCE(mm.min_marks,'') AS minMarks,COALESCE(mm.max_marks,'') AS maxMarks,
              COALESCE(mm.exam_date,'') AS examDate,COALESCE(mm.grade_subject,'') AS gradeSubject
            FROM attribute_name a LEFT JOIN tb_exam_max_min_primary mm ON mm.school_id=a.school_id AND mm.session=a.session
              AND mm.class_id=a.class_id AND mm.header_id=a.header_id AND mm.category_id=a.category_id
              AND mm.attribute_id=a.attribute_id AND mm.term=? AND mm.exam_type_id=?
            WHERE a.school_id=? AND a.session=? AND a.class_id=? AND a.header_id=? AND a.category_id=? AND a.ClassType=?
            ORDER BY COALESCE(a.attribute_order,9999),a.attribute_id
            """, value(p, "termId", "0"), value(p, "examTypeId", "0"), school, session, classId, headerId, categoryId, value(p, "classType", "Regular"));
    }

    private List<Map<String, Object>> learnerValues(String school, String session, Map<String, String> p) {
        return options(school, "learner-values", Map.of("session", session, "attributeId", value(p, "attributeId", "0")));
    }

    private List<Map<String, Object>> chartLock(String school, String session) {
        return safe("""
            SELECT cn.id AS classId,cn.class_name AS className,e.exam_type_id AS examTypeId,
              e.exam_name_dis AS examType,e.exam_order AS displayOrder,
              COALESCE(sc.show_date,'') AS showDate,COALESCE(CONVERT(varchar(10),sc.show_rank),'') AS showRank
            FROM class_name cn JOIN assign_exam_set a ON a.school_id=cn.school_id AND a.class_id=cn.id
            JOIN exam_set e ON e.school_id=a.school_id AND e.exam_set_id=a.exam_set_id
            LEFT JOIN stu_chartlock sc ON sc.school_id=cn.school_id AND sc.session=? AND sc.class_id=cn.id AND sc.exam_type_id=e.exam_type_id
            WHERE cn.school_id=? AND TRY_CONVERT(int,a.from_year)<=TRY_CONVERT(int,LEFT(?,4))
              AND (CONVERT(varchar(20),a.to_year)='0' OR TRY_CONVERT(int,a.to_year)>=TRY_CONVERT(int,RIGHT(?,4)))
            GROUP BY cn.id,cn.class_name,e.exam_type_id,e.exam_name_dis,e.exam_order,sc.show_date,sc.show_rank
            ORDER BY cn.id,COALESCE(e.exam_order,9999),e.exam_type_id
            """, session, school, session, session);
    }

    private List<Map<String, Object>> examLock(String school, String session, Map<String, String> p) {
        String sectionId = value(p, "sectionId", "0"), examTypeId = value(p, "examTypeId", "0"), subjectId = value(p, "subjectId", "0");
        return safe("""
            SELECT el.id,el.sec_id AS sectionId,el.class_id AS classId,el.subject_id AS subjectId,
              sm.Subject AS subjectName,el.exam_type_id AS examTypeId,el.term AS termId,el.lock_period AS lockPeriod
            FROM Exam_Lock el LEFT JOIN TB_SubjectMaster sm ON sm.school_id=el.school_id AND sm.id=el.subject_id
            WHERE el.school_id=? AND el.Session=? AND (?='0' OR el.sec_id=?) AND (?='0' OR el.exam_type_id=?) AND (?='0' OR el.subject_id=?)
            ORDER BY el.class_id,el.sec_id,el.exam_type_id,sm.Subject
            """, school, session, sectionId, sectionId, examTypeId, examTypeId, subjectId, subjectId);
    }

    private List<Map<String, Object>> lockClasses(String school, String session) {
        return safe("""
            SELECT DISTINCT c.sec_id AS id,c.class_id AS classId,n.class_name+'('+c.section+')' AS name,n.class_group AS classGroup
            FROM TB_Class c JOIN class_name n ON n.school_id=c.school_id AND n.id=c.class_id
            JOIN tb_exam_max_min_new mm ON mm.school_id=c.school_id AND mm.Session=? AND mm.class_id=c.class_id
            WHERE n.school_id=? AND n.class_group IN ('Junior','Senior') AND c.school_id=?
            UNION
            SELECT DISTINCT c.sec_id AS id,c.class_id AS classId,n.class_name+'('+c.section+')' AS name,n.class_group AS classGroup
            FROM TB_Class c JOIN class_name n ON n.school_id=c.school_id AND n.id=c.class_id
            JOIN tb_exam_max_min_new mm ON mm.school_id=c.school_id AND mm.Session=? AND mm.class_id=c.sec_id
            WHERE n.school_id=? AND n.class_group IN ('Junior','Senior') AND c.school_id=?
            UNION
            SELECT DISTINCT c.sec_id AS id,c.class_id AS classId,n.class_name+'('+c.section+')' AS name,n.class_group AS classGroup
            FROM TB_Class c JOIN class_name n ON n.school_id=c.school_id AND n.id=c.class_id
            JOIN TB_VirtualClass vc ON vc.school_id=c.school_id AND vc.session=? AND vc.vclass_id=c.sec_id
            JOIN tb_timetable tt ON tt.school_id=c.school_id AND tt.sec_id=vc.vclass_id
            WHERE n.school_id=? AND c.school_id=?
            ORDER BY classId,id,name
            """, session, school, school, session, school, school, session, school, school);
    }

    private List<Map<String, Object>> lockSubjects(String school, String session, String sectionId, String marksType) {
        int sec = integer(sectionId);
        String filter = "Grade".equalsIgnoreCase(marksType) ? "mm.Grade_subject='Y'" : "mm.Grade_subject IN ('N','YN')";
        return safe("""
            SELECT DISTINCT sm.id,sm.Subject AS name
            FROM TB_SubjectMaster sm
            JOIN TB_Class c ON c.school_id=sm.school_id AND c.sec_id=?
            JOIN TB_Timetable tt ON tt.school_id=sm.school_id AND tt.subject_id=sm.id
            JOIN tb_exam_max_min_new mm ON mm.school_id=sm.school_id AND mm.subject_id=sm.id
            WHERE sm.school_id=? AND mm.Session=? AND mm.class_id=CASE WHEN c.class_id IN ('17','18') THEN ? ELSE c.class_id END
              AND (tt.sec_id=? OR tt.sec_id IN (SELECT vclass_id FROM TB_VirtualClass WHERE school_id=? AND session=?))
              AND """ + " " + filter + " ORDER BY sm.Subject", sec, school, session, sec, sec, school, session);
    }

    private void saveSubject(String school, String staff, Map<String, Object> b) {
        String name = text(b.get("name")).trim().toUpperCase(), code = text(b.get("code")).trim(), practical = text(b.get("practical"));
        int id = integer(b.get("id"));
        if (name.isBlank() || code.isBlank()) throw new IllegalArgumentException("Subject name and code are required");
        if (scalarInt("SELECT COUNT(*) FROM TB_SubjectMaster WHERE school_id=? AND Subject=? AND id<>? AND status=1", school, name, id) > 0)
            throw new IllegalArgumentException("Subject name already exists");
        if (scalarInt("SELECT COUNT(*) FROM TB_SubjectMaster WHERE school_id=? AND Subject_code=? AND id<>? AND status=1", school, code, id) > 0)
            throw new IllegalArgumentException("Subject code already exists");
        if (id > 0) jdbc.update("UPDATE TB_SubjectMaster SET Subject=?,Subject_code=?,Practical=?,modified_date=GETDATE(),modified_by=? WHERE school_id=? AND id=?", name, code, practical, staff, school, id);
        else jdbc.update("INSERT INTO TB_SubjectMaster(id,Subject_code,Subject,Practical,created_date,created_by,modified_date,modified_by,school_id,status) SELECT COALESCE(MAX(id),0)+1,?,?,?,GETDATE(),?,'','',?,1 FROM TB_SubjectMaster WHERE school_id=?", code, name, practical, staff, school, school);
    }

    private void saveClassSubjects(String school, String staff, Map<String, Object> b) {
        String session = text(b.get("session")); int classId = integer(b.get("classId")), sectionId = integer(b.get("sectionId"));
        int stored = sectionId > 0 ? sectionId : classId;
        jdbc.update("UPDATE TB_ClassSubject SET status=0,modified_date=GETDATE(),modified_by=? WHERE school_id=? AND session=? AND class_id=?", staff, school, session, stored);
        for (Map<String, Object> row : rows(b)) {
            if (!truthy(row.get("selected"))) continue;
            int subject = integer(row.get("id"));
            String code = text(row.get("code")); int optional = truthy(row.get("optional")) ? 1 : 0, grade = truthy(row.get("grade")) ? 1 : 0;
            int order = integer(row.get("displayOrder"));
            if (scalarInt("SELECT COUNT(*) FROM TB_ClassSubject WHERE school_id=? AND session=? AND class_id=? AND subject_id=?", school, session, stored, subject) > 0)
                jdbc.update("UPDATE TB_ClassSubject SET subject_code=?,isoptional=?,isgrade=?,subject_order=?,status=1,modified_date=GETDATE(),modified_by=? WHERE school_id=? AND session=? AND class_id=? AND subject_id=?", code, optional, grade, order, staff, school, session, stored, subject);
            else
                jdbc.update("INSERT INTO TB_ClassSubject(class_id,subject_id,subject_code,isoptional,isgrade,isprac,subject_order,status,created_by,created_date,modified_date,modified_by,school_id,session) SELECT ?,?,?,?,? ,Practical,?,1,?,GETDATE(),GETDATE(),?, ?,? FROM TB_SubjectMaster WHERE school_id=? AND id=?", stored, subject, code, optional, grade, order, staff, staff, school, session, school, subject);
        }
    }

    private void saveSubSubjects(String school, String staff, Map<String, Object> b) {
        String session = text(b.get("session")); int classId = integer(b.get("classId")), subject = integer(b.get("subjectId"));
        jdbc.update("DELETE FROM SubSubject WHERE school_id=? AND session=? AND class_id=? AND subject_id=?", school, session, classId, subject);
        for (Object raw : list(b.get("subSubjectIds"))) {
            int sub = integer(raw); if (sub > 0) jdbc.update("INSERT INTO SubSubject(class_id,subject_id,sub_subject_id,created_date,created_by,session,school_id) VALUES(?,?,?,GETDATE(),?,?,?)", classId, subject, sub, staff, session, school);
        }
    }

    private void savePracticalSubject(String school, String staff, Map<String, Object> b) {
        String session = text(b.get("session"));
        int section = integer(b.get("sectionId")), subject = integer(b.get("subjectId")), practical = integer(b.get("practicalSubjectId"));
        if (section <= 0 || subject <= 0 || practical <= 0) throw new IllegalArgumentException("Class, subject and practical subject are required");
        if (scalarInt("SELECT COUNT(*) FROM TB_Pract_Subject WHERE school_id=? AND session=? AND sec_id=? AND subject_id=?", school, session, section, subject) > 0)
            throw new IllegalArgumentException("Already Defined");
        jdbc.update("INSERT INTO TB_Pract_Subject(subject_id,pr_subject_id,sec_id,created_date,created_by,session,school_id) VALUES(?,?,?,GETDATE(),?,?,?)", subject, practical, section, staff, session, school);
    }

    private void saveHeader(String school, String staff, Map<String, Object> b) {
        String session = text(b.get("session")), name = text(b.get("name")).trim().toUpperCase(), type = text(b.get("classType")), order = text(b.get("displayOrder")); int id = integer(b.get("id"));
        if (scalarInt("SELECT COUNT(*) FROM header_name WHERE school_id=? AND session=? AND ClassType=? AND header_name=? AND header_id<>?", school, session, type, name, id) > 0) throw new IllegalArgumentException("Header name already exists");
        if (scalarInt("SELECT COUNT(*) FROM header_name WHERE school_id=? AND session=? AND ClassType=? AND header_order=? AND header_id<>?", school, session, type, order, id) > 0) throw new IllegalArgumentException("Header order already exists");
        if (id > 0) jdbc.update("UPDATE header_name SET header_name=?,header_order=?,modified_date=GETDATE(),modified_by=? WHERE school_id=? AND session=? AND ClassType=? AND header_id=?", name, order, staff, school, session, type, id);
        else jdbc.update("INSERT INTO header_name(header_id,header_name,session,created_date,created_by,modified_date,modified_by,status,school_id,header_order,ClassType) SELECT COALESCE(MAX(header_id),0)+1,?,?,GETDATE(),?,'','',1,?,?,? FROM header_name WHERE school_id=? AND session=?", name, session, staff, school, order, type, school, session);
    }

    private void saveCategory(String school, String staff, Map<String, Object> b) {
        String session = text(b.get("session")), name = text(b.get("name")).trim(), type = text(b.get("classType")); int classId = integer(b.get("classId")), headerId = integer(b.get("headerId")), id = integer(b.get("id")), order = integer(b.get("displayOrder"));
        if (scalarInt("SELECT COUNT(*) FROM category_name WHERE school_id=? AND session=? AND class_id=? AND header_id=? AND ClassType=? AND category_name=? AND category_id<>?", school, session, classId, headerId, type, name, id) > 0) throw new IllegalArgumentException("Category name already exists");
        if (scalarInt("SELECT COUNT(*) FROM category_name WHERE school_id=? AND session=? AND class_id=? AND header_id=? AND ClassType=? AND cat_order=? AND category_id<>?", school, session, classId, headerId, type, order, id) > 0) throw new IllegalArgumentException("Category order already exists");
        if (id > 0) jdbc.update("UPDATE category_name SET category_name=?,header_id=?,cat_order=?,modified_date=GETDATE(),modified_by=? WHERE school_id=? AND session=? AND class_id=? AND ClassType=? AND category_id=?", name, headerId, order, staff, school, session, classId, type, id);
        else jdbc.update("INSERT INTO category_name(category_id,header_id,category_name,session,created_date,created_by,modified_date,modified_by,status,school_id,class_id,cat_order,ClassType) SELECT COALESCE(MAX(category_id),0)+1,?,?,GETDATE(),?,'','',1,?,?,?,? FROM category_name WHERE school_id=? AND session=?", headerId, name, session, staff, school, classId, order, type, school, session);
    }

    private void saveAttribute(String school, String staff, Map<String, Object> b) {
        String session = text(b.get("session")), name = text(b.get("name")).trim(), type = text(b.get("classType")); int classId = integer(b.get("classId")), headerId = integer(b.get("headerId")), categoryId = integer(b.get("categoryId")), id = integer(b.get("id")), order = integer(b.get("displayOrder"));
        if (scalarInt("SELECT COUNT(*) FROM attribute_name WHERE school_id=? AND session=? AND class_id=? AND header_id=? AND category_id=? AND ClassType=? AND attribute_name=? AND attribute_id<>?", school, session, classId, headerId, categoryId, type, name, id) > 0) throw new IllegalArgumentException("Attribute name already exists");
        if (scalarInt("SELECT COUNT(*) FROM attribute_name WHERE school_id=? AND session=? AND class_id=? AND header_id=? AND category_id=? AND ClassType=? AND attribute_order=? AND attribute_id<>?", school, session, classId, headerId, categoryId, type, order, id) > 0) throw new IllegalArgumentException("Attribute order already exists");
        if (id > 0) jdbc.update("UPDATE attribute_name SET attribute_name=?,category_id=?,header_id=?,attribute_order=?,modified_date=GETDATE(),modified_by=? WHERE school_id=? AND session=? AND class_id=? AND ClassType=? AND attribute_id=?", name, categoryId, headerId, order, staff, school, session, classId, type, id);
        else jdbc.update("INSERT INTO attribute_name(attribute_id,header_id,category_id,attribute_name,attribute_order,session,class_id,created_date,created_by,modified_date,modified_by,status,school_id,ClassType) SELECT COALESCE(MAX(attribute_id),0)+1,?,?,?, ?,?, ?,GETDATE(),?,'','',1,?,? FROM attribute_name WHERE school_id=?", headerId, categoryId, name, order, session, classId, staff, school, type, school);
    }

    private void saveWeightages(String school, String staff, Map<String, Object> b) {
        String session = text(b.get("session")); int classId = integer(b.get("classId")), term = integer(b.get("termId"));
        for (Map<String, Object> row : rows(b)) {
            int examId = integer(row.get("examId")), examType = integer(row.get("examTypeId")); String weight = text(row.get("weightage"));
            if (scalarInt("SELECT COUNT(*) FROM TB_WeightageMarks_new WHERE school_id=? AND session=? AND class_id=? AND exam_id=? AND exam_type_id=? AND term=?", school, session, classId, examId, examType, term) > 0)
                jdbc.update("UPDATE TB_WeightageMarks_new SET weightage=?,modified_date=GETDATE(),modified_by=? WHERE school_id=? AND session=? AND class_id=? AND exam_id=? AND exam_type_id=? AND term=?", weight, staff, school, session, classId, examId, examType, term);
            else jdbc.update("INSERT INTO TB_WeightageMarks_new(class_id,exam_id,exam_type_id,term,weightage,created_date,created_by,modified_date,modified_by,school_id,session) VALUES(?,?,?,?,?,GETDATE(),?,GETDATE(),?,?,?)", classId, examId, examType, term, weight, staff, staff, school, session);
        }
    }

    private void saveMaxMin(String school, String staff, Map<String, Object> b) {
        String session = text(b.get("session")); int classId = integer(b.get("classId")), sectionId = integer(b.get("sectionId")), term = integer(b.get("termId")); int stored = sectionId > 0 ? sectionId : classId;
        for (Map<String, Object> row : rows(b)) {
            int subject = integer(row.get("subjectId")), exam = integer(row.get("examTypeId")), examId = integer(row.get("examId"));
            String min = text(row.get("minMarks")), max = text(row.get("maxMarks")), date = text(row.get("examDate")), grade = text(row.get("gradeSubject"));
            if (scalarInt("SELECT COUNT(*) FROM tb_exam_max_min_new WHERE school_id=? AND Session=? AND class_id=? AND subject_id=? AND exam_id=? AND Exam_type_id=? AND Term_id=?", school, session, stored, subject, examId, exam, term) > 0)
                jdbc.update("UPDATE tb_exam_max_min_new SET min_marks=?,max_marks=?,E_Date=?,Grade_subject=?,modified_date=GETDATE(),modified_by=? WHERE school_id=? AND Session=? AND class_id=? AND subject_id=? AND exam_id=? AND Exam_type_id=? AND Term_id=?", nullableNumber(min), nullableNumber(max), date, grade, staff, school, session, stored, subject, examId, exam, term);
            else jdbc.update("INSERT INTO tb_exam_max_min_new(id,class_id,sec_id,subject_id,min_marks,max_marks,Grade_subject,E_Date,Exam_id,Exam_type_id,Term_id,created_date,created_by,modified_date,modified_by,school_id,Session) SELECT COALESCE(MAX(id),0)+1,?,?,?,?,?,?,?,?,?,?,GETDATE(),?,'','',?,? FROM tb_exam_max_min_new WHERE school_id=?", stored, sectionId, subject, nullableNumber(min), nullableNumber(max), grade, date, examId, exam, term, staff, school, session, school);
        }
    }

    private void savePrimaryMaxMin(String school, String staff, Map<String, Object> b) {
        String session = text(b.get("session")), classId = text(b.get("classId")), headerId = text(b.get("headerId")), categoryId = text(b.get("categoryId")), term = text(b.get("termId")), exam = text(b.get("examTypeId"));
        for (Map<String, Object> row : rows(b)) {
            String attribute = text(row.get("attributeId")), min = text(row.get("minMarks")), max = text(row.get("maxMarks")), date = text(row.get("examDate")), grade = text(row.get("gradeSubject"));
            if (scalarInt("SELECT COUNT(*) FROM tb_exam_max_min_primary WHERE school_id=? AND session=? AND class_id=? AND header_id=? AND category_id=? AND attribute_id=? AND term=? AND exam_type_id=?", school, session, classId, headerId, categoryId, attribute, term, exam) > 0)
                jdbc.update("UPDATE tb_exam_max_min_primary SET min_marks=?,max_marks=?,exam_date=?,grade_subject=?,modified_date=?,modified_by=? WHERE school_id=? AND session=? AND class_id=? AND header_id=? AND category_id=? AND attribute_id=? AND term=? AND exam_type_id=?", min, max, date, grade, nowText(), staff, school, session, classId, headerId, categoryId, attribute, term, exam);
            else jdbc.update("INSERT INTO tb_exam_max_min_primary(class_id,header_id,category_id,attribute_id,min_marks,max_marks,grade_subject,exam_date,term,exam_type_id,created_date,created_by,modified_date,modified_by,school_id,session) VALUES(?,?,?,?,?,?,?,?,?,?,?, ?,?,?,?,?)", classId, headerId, categoryId, attribute, min, max, grade, date, term, exam, nowText(), staff, nowText(), staff, school, session);
        }
    }

    private void saveLearnerValue(String school, String staff, Map<String, Object> b) {
        String session = text(b.get("session")), value = text(b.get("name")).trim(); int attribute = integer(b.get("attributeId")), id = integer(b.get("id"));
        if (scalarInt("SELECT COUNT(*) FROM LERNERPROFILE_ATTRIBUTE_Value WHERE school_id=? AND session=? AND ATTRIBUTE_ID=? AND Attribute_Value=? AND id<>? AND status=1", school, session, attribute, value, id) > 0) throw new IllegalArgumentException("Attribute value already exists");
        if (id > 0) jdbc.update("UPDATE LERNERPROFILE_ATTRIBUTE_Value SET Attribute_Value=? WHERE school_id=? AND session=? AND ATTRIBUTE_ID=? AND id=? AND status=1", value, school, session, attribute, id);
        else jdbc.update("INSERT INTO LERNERPROFILE_ATTRIBUTE_Value(ATTRIBUTE_ID,Attribute_Value_Id,Attribute_Value,school_id,created_date,created_by,status,session) SELECT ?,CONVERT(varchar(100),COALESCE(MAX(TRY_CONVERT(int,Attribute_Value_Id)),0)+1),?,?,GETDATE(),?,1,? FROM LERNERPROFILE_ATTRIBUTE_Value", attribute, value, school, staff, session);
    }

    private void saveIndicatorCategory(String school, Map<String, Object> b) {
        String name = text(b.get("name")).trim().toUpperCase(), type = text(b.get("classType"));
        if (scalarInt("SELECT COUNT(*) FROM tb_indicatorCAT WHERE school_id=? AND indicator_category=? AND ClassType=? AND status='active'", school, name, type) == 0)
            jdbc.update("INSERT INTO tb_indicatorCAT(school_id,indicator_category,status,ClassType) VALUES(?,?,?,?)", school, name, "active", type);
    }

    private void saveIndicator(String school, String staff, Map<String, Object> b) {
        String session = text(b.get("session")), name = text(b.get("name")).trim().toUpperCase(), categoryName = text(b.get("categoryName")); int id = integer(b.get("id")), classId = integer(b.get("classId")), categoryId = integer(b.get("categoryId")), order = integer(b.get("displayOrder"));
        if (scalarInt("SELECT COUNT(*) FROM TB_INDICATOR WHERE school_id=? AND session=? AND class_id=? AND indicator_category_id=? AND IndicatorOrder=? AND status='active' AND ID<>?", school, session, classId, categoryId, order, id) > 0) throw new IllegalArgumentException("Indicator order already exists");
        if (scalarInt("SELECT COUNT(*) FROM TB_INDICATOR WHERE school_id=? AND session=? AND class_id=? AND indicator_category_id=? AND indicator_Name=? AND status='active' AND ID<>?", school, session, classId, categoryId, name, id) > 0) throw new IllegalArgumentException("Indicator name already exists");
        if (id > 0) jdbc.update("UPDATE TB_INDICATOR SET indicator_cat=?,indicator_Name=?,IndicatorOrder=?,class_id=?,indicator_category_id=?,modified_by=?,modified_date=GETDATE() WHERE ID=? AND school_id=? AND session=?", categoryName, name, order, classId, categoryId, staff, id, school, session);
        else jdbc.update("INSERT INTO TB_INDICATOR(indicator_ID,school_id,session,indicator_cat,indicator_Name,IndicatorOrder,status,created_by,created_date,modified_by,modified_date,class_id,indicator_category_id) SELECT COALESCE(MAX(ID),0)+1,?,?, ?,?,?, 'active',?,GETDATE(),'','',?,? FROM TB_INDICATOR WHERE school_id=?", school, session, categoryName, name, order, staff, classId, categoryId, school);
    }

    private void saveChartLock(String school, String staff, Map<String, Object> b) {
        String session = text(b.get("session"));
        for (Map<String, Object> row : rows(b)) {
            int classId = integer(row.get("classId")), exam = integer(row.get("examTypeId")); String date = text(row.get("showDate")), rank = text(row.get("showRank"));
            if (date.isBlank()) {
                if (scalarInt("SELECT COUNT(*) FROM stu_chartlock WHERE school_id=? AND session=? AND class_id=? AND exam_type_id=?", school, session, classId, exam) > 0) {
                    jdbc.update("INSERT INTO stu_chartlock_log(exam_type_id,show_date,session,class_id,show_rank,school_id,created_by,created_date,modified_date,modified_by,logBy,logCreateDate) SELECT exam_type_id,show_date,session,class_id,show_rank,school_id,created_by,created_date,modified_date,modified_by,?,GETDATE() FROM stu_chartlock WHERE school_id=? AND session=? AND class_id=? AND exam_type_id=?", staff, school, session, classId, exam);
                    jdbc.update("DELETE FROM stu_chartlock WHERE school_id=? AND session=? AND exam_type_id=? AND class_id=?", school, session, exam, classId);
                }
            } else if (scalarInt("SELECT COUNT(*) FROM stu_chartlock WHERE school_id=? AND session=? AND class_id=? AND exam_type_id=? AND show_date<>?", school, session, classId, exam, date) > 0)
                jdbc.update("UPDATE stu_chartlock SET show_date=?,show_rank=?,modified_date=GETDATE(),modified_by=? WHERE school_id=? AND session=? AND class_id=? AND exam_type_id=?", date, boolInt(rank), staff, school, session, classId, exam);
            else if (scalarInt("SELECT COUNT(*) FROM stu_chartlock WHERE school_id=? AND session=? AND class_id=? AND exam_type_id=?", school, session, classId, exam) == 0)
                jdbc.update("INSERT INTO stu_chartlock(exam_type_id,show_date,session,class_id,show_rank,school_id,created_by,modified_date,modified_by) VALUES(?,?,?,?,?,?,GETDATE(),?,'',?)", exam, date, session, classId, boolInt(rank), school, staff, staff);
        }
        jdbc.update("DELETE FROM stu_chartlock WHERE school_id=? AND show_date=''", school);
    }

    private void saveExamLock(String school, String staff, Map<String, Object> b) {
        if (b.containsKey("defaultDay") && !b.containsKey("sectionId")) {
            saveDefaultLock(school, text(b.get("session")), text(b.get("defaultDay")), staff);
            return;
        }
        String session = text(b.get("session")); int section = integer(b.get("sectionId")), classId = integer(b.get("classId")), exam = integer(b.get("examTypeId")), subject = integer(b.get("subjectId")), term = integer(b.get("termId")), lock = integer(b.get("lockPeriod"));
        int fId = integer(b.get("fId"));
        if (scalarInt("SELECT COUNT(*) FROM Exam_Lock WHERE school_id=? AND Session=? AND sec_id=? AND exam_type_id=? AND subject_id=? AND f_id=?", school, session, section, exam, subject, fId) > 0)
            jdbc.update("UPDATE Exam_Lock SET lock_period=?,modified_date=GETDATE(),modified_by=? WHERE school_id=? AND Session=? AND sec_id=? AND exam_type_id=? AND subject_id=? AND f_id=?", lock, staff, school, session, section, exam, subject, fId);
        else {
            int examId = scalarInt("SELECT TOP 1 exam_id FROM exam_type WHERE school_id=? AND exam_type_id=?", school, exam);
            int effectiveTerm = scalarInt("SELECT TOP 1 e.term_id FROM exam_set e JOIN assign_exam_set a ON a.school_id=e.school_id AND a.exam_set_id=e.exam_set_id WHERE e.school_id=? AND e.exam_type_id=? AND a.class_id=? AND a.to_year='0'", school, exam, classId);
            if (effectiveTerm == 0) effectiveTerm = term;
            jdbc.update("INSERT INTO Exam_Lock(f_id,class_id,sec_id,subject_id,exam_id,exam_type_id,term,lock_period,created_date,created_by,modified_date,modified_by,school_id,Session) VALUES(?,?,?,?,?,?,?, ?,GETDATE(),?,'','',?,?)", fId, classId, section, subject, examId, exam, effectiveTerm, lock, staff, school, session);
        }
    }

    private void saveDefaultLock(String school, String session, String day, String staff) {
        if (scalarInt("SELECT COUNT(*) FROM default_lock WHERE school_id=?", school) > 0) {
            jdbc.update("UPDATE default_lock SET default_day=?,created_date=GETDATE() WHERE school_id=?", day, school);
            jdbc.update("UPDATE Exam_Lock SET lock_period=?,modified_date=GETDATE(),modified_by=? WHERE school_id=? AND Session=?", integer(day), staff, school, session);
        } else jdbc.update("INSERT INTO default_lock(default_day,school_id,status,created_date) VALUES(?,?,1,GETDATE())", day, school);
    }

    private Map<String, Object> defaultLock(String school) {
        List<Map<String, Object>> rows = safe("SELECT TOP 1 default_day AS defaultDay FROM default_lock WHERE school_id=? AND (status IS NULL OR status=1)", school);
        return rows.isEmpty() ? new LinkedHashMap<>() : rows.get(0);
    }

    private List<Map<String, Object>> examTypes(String school, String session, String classId, String termId) {
        return safe("""
            SELECT e.exam_type_id AS id,e.exam_name_dis AS name,et.exam_id AS examId,e.exam_order AS displayOrder,e.term_id AS termId
            FROM exam_set e JOIN exam_type et ON et.school_id=e.school_id AND et.exam_type_id=e.exam_type_id
            JOIN assign_exam_set a ON a.school_id=e.school_id AND a.exam_set_id=e.exam_set_id
            WHERE e.school_id=? AND e.term_id=? AND a.class_id=?
              AND TRY_CONVERT(int,a.from_year)<=TRY_CONVERT(int,LEFT(?,4))
              AND (CONVERT(varchar(20),a.to_year)='0' OR TRY_CONVERT(int,a.to_year)>=TRY_CONVERT(int,RIGHT(?,4)))
            ORDER BY COALESCE(e.exam_order,9999),e.exam_type_id
            """, school, termId.isBlank() ? "0" : termId, classId.isBlank() ? "0" : classId, session, session);
    }

    private List<Map<String, Object>> terms(String school, String session, String classId) {
        if (classId == null || classId.isBlank() || "0".equals(classId)) return safe("SELECT DISTINCT st.term AS id,COALESCE(t.term_name,CONVERT(varchar(20),st.term)) AS name FROM session_term st LEFT JOIN term t ON t.term_id=st.term WHERE st.school_id=? AND st.Session=? ORDER BY st.term", school, session);
        return safe("SELECT DISTINCT e.term_id AS id,COALESCE(t.term_name,CONVERT(varchar(20),e.term_id)) AS name FROM exam_set e JOIN assign_exam_set a ON a.school_id=e.school_id AND a.exam_set_id=e.exam_set_id LEFT JOIN term t ON t.term_id=e.term_id WHERE e.school_id=? AND a.class_id=? AND TRY_CONVERT(int,a.from_year)<=TRY_CONVERT(int,LEFT(?,4)) AND (CONVERT(varchar(20),a.to_year)='0' OR TRY_CONVERT(int,a.to_year)>=TRY_CONVERT(int,RIGHT(?,4))) ORDER BY e.term_id", school, classId, session, session);
    }

    private String defaultSession(String school) {
        List<Map<String, Object>> r = safe("SELECT TOP 1 session FROM TB_Session WHERE school_id=? AND status='Default' ORDER BY id DESC", school);
        if (!r.isEmpty()) return text(r.get(0).get("session"));
        r = safe("SELECT TOP 1 session FROM TB_Session WHERE school_id=? ORDER BY id DESC", school);
        return r.isEmpty() ? "" : text(r.get(0).get("session"));
    }

    private List<Map<String, Object>> safe(String sql, Object... args) {
        try { return jdbc.queryForList(sql, args); } catch (DataAccessException ex) { return new ArrayList<>(); }
    }
    private int scalarInt(String sql, Object... args) { try { Integer n = jdbc.queryForObject(sql, Integer.class, args); return n == null ? 0 : n; } catch (Exception e) { return 0; } }
    private String scalarText(String sql, Object... args) { try { String n = jdbc.queryForObject(sql, String.class, args); return n == null ? "" : n; } catch (Exception e) { return ""; } }
    private static String value(Map<String, String> p, String k, String d) { String v = p == null ? null : p.get(k); return v == null ? d : v; }
    private static int valueInt(Map<String, String> p, String k, int d) { try { return Integer.parseInt(value(p,k,String.valueOf(d))); } catch (Exception e) { return d; } }
    private static String text(Object v) { return v == null ? "" : String.valueOf(v); }
    private static int integer(Object v) { try { return Integer.parseInt(text(v).replaceAll("\\.0+$", "")); } catch (Exception e) { return 0; } }
    private static boolean truthy(Object v) { return "1".equals(text(v)) || "true".equalsIgnoreCase(text(v)) || "yes".equalsIgnoreCase(text(v)) || "y".equalsIgnoreCase(text(v)); }
    private static int boolInt(String v) { return truthy(v) ? 1 : 0; }
    private static Object nullableNumber(String v) { return v == null || v.isBlank() ? null : v; }
    private static String nowText() { return new java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.ENGLISH).format(new java.util.Date()); }
    @SuppressWarnings("unchecked") private static List<Map<String, Object>> rows(Map<String, Object> b) { Object v = b.get("rows"); return v instanceof List<?> l ? (List<Map<String, Object>>) (List<?>) l : List.of(); }
    private static List<?> list(Object v) { return v instanceof List<?> l ? l : List.of(); }
}
