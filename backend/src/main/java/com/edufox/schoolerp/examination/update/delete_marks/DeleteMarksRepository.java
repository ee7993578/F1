package com.edufox.schoolerp.examination.update.delete_marks;
import com.edufox.schoolerp.examination.report.examination_report.*;
import com.edufox.schoolerp.examination.setup.exam_setup.*;
import com.edufox.schoolerp.examination.work.examination_work.*;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class DeleteMarksRepository {
    private final JdbcTemplate jdbc;

    public Map<String, Object> bootstrap(String school, String session) {
        String activeSession = session == null || session.isBlank() ? defaultSession(school) : session;
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("session", activeSession);
        out.put("classes", safe("""
            SELECT tc.sec_id AS id,cn.class_name+' ('+tc.section+')' AS name,tc.class_id AS classId
            FROM TB_Class tc JOIN class_name cn ON cn.school_id=tc.school_id AND cn.id=tc.class_id
            WHERE tc.school_id=? AND cn.school_id=?
            UNION
            SELECT DISTINCT vc.VClass_id AS id,vc.V_Class AS name,vc.VClass_id AS classId
            FROM TB_VirtualClass vc
            WHERE vc.school_id=? AND vc.session=?
              AND vc.sec_id IN (SELECT sec_id FROM TB_Class WHERE school_id=?)
            ORDER BY classId,id,name
            """, school, school, school, activeSession, school));
        return out;
    }

    public List<Map<String, Object>> terms(String school, String session, int sectionId) {
        int classId = classForSelection(school, session, sectionId);
        return safe("""
            SELECT DISTINCT e.term_id AS id,COALESCE(t.term_name,CONVERT(varchar(20),e.term_id)) AS name
            FROM exam_set e JOIN assign_exam_set a ON a.school_id=e.school_id AND a.exam_set_id=e.exam_set_id
            LEFT JOIN term t ON t.term_id=e.term_id
            WHERE e.school_id=? AND a.class_id=?
              AND TRY_CONVERT(int,a.from_year)<=TRY_CONVERT(int,LEFT(?,4))
              AND (a.to_year=0 OR TRY_CONVERT(int,a.to_year)>=TRY_CONVERT(int,RIGHT(?,4)))
            ORDER BY e.term_id
            """, school, classId, session, session);
    }

    public List<Map<String, Object>> subjects(String school, String session, int sectionId) {
        if (isVirtual(school, session, sectionId)) {
            return safe("""
                SELECT DISTINCT sm.id,sm.Subject AS name
                FROM TB_SubjectMaster sm JOIN TB_VirtualClass vc ON vc.school_id=sm.school_id AND vc.subject_id=sm.id
                WHERE sm.school_id=? AND vc.session=? AND vc.VClass_id=?
                ORDER BY sm.Subject
                """, school, session, sectionId);
        }
        return safe("""
            SELECT DISTINCT sm.id,sm.Subject AS name
            FROM TB_SubjectMaster sm JOIN senior_marks m ON m.school_id=sm.school_id AND TRY_CONVERT(int,m.Subject_id)=sm.id
            WHERE sm.school_id=? AND m.Session=? AND m.Stu_id IN
              (SELECT CONVERT(varchar(50),stu_id) FROM TB_StudentMaster WHERE school_id=? AND session=? AND sec_id=?)
            ORDER BY sm.Subject
            """, school, session, school, session, sectionId);
    }

    public List<Map<String, Object>> examTypes(String school, String session, int sectionId, int termId) {
        int classId = classForSelection(school, session, sectionId);
        return safe("""
            SELECT DISTINCT e.exam_type_id AS id,e.exam_name_dis AS name,et.exam_id AS examId,e.exam_order AS displayOrder
            FROM exam_set e JOIN exam_type et ON et.school_id=e.school_id AND et.exam_type_id=e.exam_type_id
            JOIN assign_exam_set a ON a.school_id=e.school_id AND a.exam_set_id=e.exam_set_id
            WHERE e.school_id=? AND e.term_id=? AND a.class_id=?
              AND TRY_CONVERT(int,a.from_year)<=TRY_CONVERT(int,LEFT(?,4))
              AND (a.to_year=0 OR TRY_CONVERT(int,a.to_year)>=TRY_CONVERT(int,RIGHT(?,4)))
            ORDER BY e.exam_order,e.exam_type_id
            """, school, termId, classId, session, session);
    }

    public Map<String, Object> classRows(String school, String session, int sectionId, String marksType, int examTypeId, int subjectId) {
        List<Map<String, Object>> rows;
        if (isVirtual(school, session, sectionId)) {
            rows = safe("""
                SELECT sm.stu_id AS stuId,si.Registration_NO AS registrationNo,
                  LTRIM(RTRIM(COALESCE(si.First_name,'')+' '+COALESCE(si.Mid_name,'')+' '+COALESCE(si.Last_name,''))) AS name,
                  COALESCE(mk.marks,'') AS marks
                FROM TB_StudentMaster sm LEFT JOIN student_other_info si ON si.school_id=sm.school_id AND si.Registration_NO=sm.Registration_NO
                OUTER APPLY (SELECT TOP 1 CASE WHEN ISNULL(NULLIF(m.absentism,''),'')<>'' THEN m.absentism ELSE m.marks END AS marks
                  FROM senior_marks m WHERE m.school_id=? AND m.Session=? AND m.exam_type_id=? AND m.subject_id=?
                    AND m.Stu_id=CONVERT(varchar(50),sm.stu_id) ORDER BY m.R_id DESC) mk
                WHERE sm.school_id=? AND sm.session=? AND LOWER(sm.status)='registered'
                  AND sm.stu_id IN (SELECT vc.Stu_id FROM TB_VirtualClass vc WHERE vc.school_id=? AND vc.session=? AND vc.VClass_id=?)
                ORDER BY name,sm.stu_id
                """, school, session, examTypeId, subjectId, school, session, school, session, sectionId);
        } else {
            rows = safe("""
                SELECT sm.stu_id AS stuId,si.Registration_NO AS registrationNo,
                  LTRIM(RTRIM(COALESCE(si.First_name,'')+' '+COALESCE(si.Mid_name,'')+' '+COALESCE(si.Last_name,''))) AS name,
                  COALESCE(mk.marks,'') AS marks
                FROM TB_StudentMaster sm LEFT JOIN student_other_info si ON si.school_id=sm.school_id AND si.Registration_NO=sm.Registration_NO
                OUTER APPLY (SELECT TOP 1 CASE WHEN ISNULL(NULLIF(m.absentism,''),'')<>'' THEN m.absentism ELSE m.marks END AS marks
                  FROM senior_marks m WHERE m.school_id=? AND m.Session=? AND m.exam_type_id=? AND m.subject_id=?
                    AND m.Stu_id=CONVERT(varchar(50),sm.stu_id) ORDER BY m.R_id DESC) mk
                WHERE sm.school_id=? AND sm.session=? AND LOWER(sm.status)='registered' AND sm.sec_id=?
                ORDER BY name,sm.stu_id
                """, school, session, examTypeId, subjectId, school, session, sectionId);
        }
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("rows", rows);
        out.put("maxMin", maxMin(school, session, sectionId, subjectId, examTypeId));
        return out;
    }

    public List<Map<String, Object>> studentSearch(String school, String session, String prefix) {
        String like = prefix == null ? "" : prefix.trim() + "%";
        return safe("""
            SELECT TOP 20 sm.stu_id AS stuId,sm.Registration_NO AS registrationNo,
              LTRIM(RTRIM(COALESCE(si.First_name,'')+' '+COALESCE(si.Mid_name,'')+' '+COALESCE(si.Last_name,''))) AS name,
              cn.class_name+' ('+c.section+')' AS classSection,COALESCE(si.Father_name,'') AS fatherName,
              COALESCE(si.Phone,si.Mobile,'') AS phoneNo,COALESCE(si.studentimage,'') AS photoUrl
            FROM TB_StudentMaster sm
            LEFT JOIN student_other_info si ON si.school_id=sm.school_id AND si.Registration_NO=sm.Registration_NO
            LEFT JOIN TB_Class c ON c.school_id=sm.school_id AND c.sec_id=sm.sec_id
            LEFT JOIN class_name cn ON cn.school_id=c.school_id AND cn.id=c.class_id
            WHERE sm.school_id=? AND sm.session=? AND LOWER(sm.status)='registered'
              AND (sm.Registration_NO LIKE ? OR si.First_name+' '+COALESCE(si.Last_name,'') LIKE ?)
            ORDER BY name,sm.stu_id
            """, school, session, like, like);
    }

    public List<Map<String, Object>> studentRows(String school, String session, String registrationNo) {
        return safe("""
            SELECT m.Stu_id AS stuId,m.sec_id AS secId,m.Subject_id AS subjectId,
              m.Term_id AS termId,m.Exam_type_id AS examTypeId,m.marks AS marks,m.max_marks AS maxMarks,
              sm.Subject AS subject,COALESCE(ex.examName,'') AS examType,
              LTRIM(RTRIM(COALESCE(f.First_name,'')+' '+COALESCE(f.Mid_name,'')+' '+COALESCE(f.Last_name,''))) AS facultyName
            FROM senior_marks m
            JOIN TB_StudentMaster st ON st.school_id=m.school_id AND st.session=m.Session AND st.stu_id=TRY_CONVERT(int,m.Stu_id)
            LEFT JOIN TB_SubjectMaster sm ON sm.school_id=m.school_id AND sm.id=TRY_CONVERT(int,m.Subject_id)
            LEFT JOIN tb_facultymaster f ON f.school_id=m.school_id AND f.f_id=TRY_CONVERT(int,m.F_id)
            OUTER APPLY (SELECT TOP 1 e.exam_name_dis AS examName
              FROM exam_set e JOIN assign_exam_set a ON a.school_id=e.school_id AND a.exam_set_id=e.exam_set_id
              WHERE e.school_id=m.school_id AND e.exam_type_id=TRY_CONVERT(int,m.Exam_type_id) AND e.term_id=TRY_CONVERT(int,m.Term_id)
                AND a.class_id=st.class_id AND TRY_CONVERT(int,a.from_year)<=TRY_CONVERT(int,LEFT(m.Session,4))
                AND (a.to_year=0 OR TRY_CONVERT(int,a.to_year)>=TRY_CONVERT(int,RIGHT(m.Session,4)))
              ORDER BY COALESCE(e.exam_order,9999),e.id) ex
            WHERE m.school_id=? AND m.Session=? AND st.Registration_NO=?
            ORDER BY sm.Subject,m.Term_id,m.Exam_type_id,m.R_id
            """, school, session, registrationNo);
    }

    public void delete(String school, String session, String staff, List<Map<String, Object>> rows) {
        for (Map<String, Object> row : rows) {
            int stuId = integer(row.get("stuId"));
            int sectionId = integer(row.get("sectionId"));
            int subjectId = integer(row.get("subjectId"));
            int examTypeId = integer(row.get("examTypeId"));
            if (stuId <= 0 || sectionId <= 0 || subjectId <= 0 || examTypeId <= 0) continue;
            String membership = isVirtual(school, session, sectionId)
                ? "EXISTS (SELECT 1 FROM TB_VirtualClass v WHERE v.school_id=m.school_id AND v.session=m.Session AND v.VClass_id=? AND v.Stu_id=TRY_CONVERT(int,m.Stu_id))"
                : "EXISTS (SELECT 1 FROM TB_StudentMaster st WHERE st.school_id=m.school_id AND st.session=m.Session AND st.sec_id=? AND st.stu_id=TRY_CONVERT(int,m.Stu_id))";
            String where = "m.school_id=? AND m.Session=? AND m.Exam_type_id=? AND m.Subject_id=? AND m.Stu_id=? AND " + membership;
            Object[] args = { school, session, examTypeId, subjectId, String.valueOf(stuId), sectionId };
            Object[] logArgs = new Object[args.length + 1];
            logArgs[0] = staff;
            System.arraycopy(args, 0, logArgs, 1, args.length);
            String logSql = """
                INSERT INTO senior_marks_log(sec_id,Stu_id,Subject_id,Exam_type_id,Session,marks,min_marks,max_marks,Term_id,F_id,school_id,created_by,created_date,modified_by,modified_date,absentism,transfer_data,deleted_date,deleted_by)
                SELECT m.sec_id,m.Stu_id,m.Subject_id,m.Exam_type_id,m.Session,m.marks,m.min_marks,m.max_marks,m.Term_id,m.F_id,m.school_id,m.created_by,m.created_date,m.modified_by,m.modified_date,m.absentism,m.transfer_data,CONVERT(varchar(50),GETDATE(),120),?
                FROM senior_marks m WHERE
                """ + where;
            jdbc.update(logSql, logArgs);
            jdbc.update("DELETE FROM senior_marks WHERE " + where, args);
        }
    }

    private Map<String, Object> maxMin(String school, String session, int sectionId, int subjectId, int examTypeId) {
        int classId = classForSelection(school, session, sectionId);
        int stored = (classId == 17 || classId == 18) ? sectionId : classId;
        List<Map<String, Object>> rows = safe("""
            SELECT TOP 1 min_marks AS minMarks,max_marks AS maxMarks,E_Date AS examDate,Grade_subject AS gradeSubject
            FROM tb_exam_max_min_new WHERE school_id=? AND Session=? AND class_id=? AND subject_id=? AND exam_type_id=?
            ORDER BY id DESC
            """, school, session, stored, subjectId, examTypeId);
        return rows.isEmpty() ? null : rows.get(0);
    }

    private boolean isVirtual(String school, String session, int sectionId) { return scalarInt("SELECT COUNT(*) FROM TB_VirtualClass WHERE school_id=? AND session=? AND VClass_id=?", school, session, sectionId) > 0; }
    private int classForSelection(String school, String session, int sectionId) {
        if (isVirtual(school, session, sectionId)) return scalarInt("SELECT TOP 1 class_id FROM TB_Class WHERE school_id=? AND sec_id=(SELECT TOP 1 sec_id FROM TB_VirtualClass WHERE school_id=? AND session=? AND VClass_id=?)", school, school, session, sectionId);
        return scalarInt("SELECT TOP 1 class_id FROM TB_Class WHERE school_id=? AND sec_id=?", school, sectionId);
    }
    private int scalarInt(String sql, Object... args) { try { Integer n = jdbc.queryForObject(sql, Integer.class, args); return n == null ? 0 : n; } catch (Exception ignored) { return 0; } }
    private String defaultSession(String school) {
        List<Map<String, Object>> rows = safe("SELECT TOP 1 session FROM TB_Session WHERE school_id=? AND status='Default' ORDER BY id DESC", school);
        if (!rows.isEmpty()) return String.valueOf(rows.get(0).get("session"));
        rows = safe("SELECT TOP 1 session FROM TB_Session WHERE school_id=? ORDER BY id DESC", school);
        return rows.isEmpty() ? "" : String.valueOf(rows.get(0).get("session"));
    }
    private List<Map<String, Object>> safe(String sql, Object... args) { try { return jdbc.queryForList(sql, args); } catch (DataAccessException ignored) { return new ArrayList<>(); } }
    private static int integer(Object value) { try { return Integer.parseInt(String.valueOf(value)); } catch (Exception ignored) { return 0; } }
}
