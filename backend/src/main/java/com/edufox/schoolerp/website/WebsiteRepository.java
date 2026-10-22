package com.edufox.schoolerp.website;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class WebsiteRepository {
    private final JdbcTemplate jdbc;

    public List<Map<String, Object>> sessions(String school) {
        return jdbc.queryForList("SELECT session AS value,session AS label,status FROM tb_session WHERE school_id=? ORDER BY CASE WHEN status='Default' THEN 0 ELSE 1 END,session DESC", school);
    }

    public List<Map<String, Object>> classes(String school) {
        return jdbc.queryForList("SELECT id,class_name AS name,class_name AS label FROM class_name WHERE school_id=? ORDER BY id", school);
    }

    public List<Map<String, Object>> sections(String school) {
        return jdbc.queryForList("SELECT c.sec_id AS id,c.class_id AS classId,cn.class_name+' ('+c.section+')' AS label,cn.class_name AS className,c.section FROM tb_class c JOIN class_name cn ON cn.id=c.class_id AND cn.school_id=c.school_id WHERE c.school_id=? ORDER BY c.class_id,c.section,c.sec_id", school);
    }

    public List<Map<String, Object>> students(String school, String session, String sectionIds) {
        String sql = "SELECT st.stu_id AS id,st.stu_id AS studentId,st.registration_no AS registrationNo,UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,''))))+' ('+cn.class_name+' '+cl.section+')' AS name,cn.class_name AS className,cl.section,st.class_id AS classId,st.sec_id AS secId FROM TB_StudentMaster st JOIN student_other_info so ON so.uid=st.uid AND so.registration_no=st.registration_no AND so.school_id=st.school_id JOIN tb_class cl ON cl.sec_id=st.sec_id AND cl.school_id=st.school_id JOIN class_name cn ON cn.id=st.class_id AND cn.school_id=st.school_id WHERE st.school_id=? AND st.session=? AND LOWER(st.status)='registered'";
        Object[] params = new Object[]{school, session};
        if (!blank(sectionIds)) sql += " AND st.sec_id IN (" + numbers(sectionIds) + ")";
        return jdbc.queryForList(sql + " ORDER BY name", params);
    }

    public List<Map<String, Object>> examOptions(String school, String session, String classId) {
        return jdbc.queryForList("SELECT DISTINCT e.exam_type_id AS id,e.exam_name_dis AS name,e.exam_order AS examOrder FROM exam_set e JOIN assign_exam_set a ON a.exam_set_id=e.exam_set_id AND a.school_id=e.school_id WHERE e.school_id=? AND a.class_id=? AND a.from_year<=TRY_CONVERT(int,LEFT(?,4)) AND (a.to_year='0' OR a.to_year>=TRY_CONVERT(int,RIGHT(?,4))) ORDER BY e.exam_order,e.exam_type_id", school, classId, session, session);
    }

    public static boolean blank(String value) { return value == null || value.trim().isEmpty(); }

    public static String numbers(String raw) {
        String clean = raw == null ? "" : raw.replaceAll("[^0-9,]", "");
        return clean.isBlank() ? "0" : clean;
    }
}
