package com.edufox.schoolerp.student;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class ClassRepresentativeRepository {
    private final JdbcTemplate jdbc;

    public Map<String,Object> list(String schoolId,String session) {
        List<Map<String,Object>> classes=jdbc.queryForList("""
          SELECT c.class_id AS classId,cn.class_name AS className,cr.f_id AS representativeId,
                 UPPER(REPLACE(LTRIM(RTRIM(COALESCE(f.First_name,'')+' '+COALESCE(f.Mid_name,'')+' '+COALESCE(f.Last_name,''))),'  ',' ')) AS representativeName
          FROM (SELECT DISTINCT class_id,school_id FROM tb_class) c
          LEFT JOIN class_name cn ON cn.id=c.class_id AND cn.school_id=c.school_id
          LEFT JOIN class_Representative cr ON cr.class_id=c.class_id AND cr.school_id=c.school_id AND cr.session=?
          LEFT JOIN tb_facultymaster f ON f.f_id=cr.f_id AND f.school_id=c.school_id
          WHERE c.school_id=? ORDER BY c.class_id
          """,session,schoolId);
        List<Map<String,Object>> faculty=jdbc.queryForList("""
          SELECT DISTINCT f.f_id AS id,
             LTRIM(RTRIM(COALESCE(f.First_name,'')+' '+COALESCE(f.Mid_name,'')+' '+COALESCE(f.Last_name,''))) AS name
          FROM tb_facultymaster f
          WHERE f.school_id=? AND f.f_id IN
            (SELECT fw.f_id FROM tb_faculty_withdrawn fw WHERE fw.school_id=? AND fw.session=? AND LOWER(fw.status)='registered')
          ORDER BY name
          """,schoolId,schoolId,session);
        return Map.of("classes",classes,"faculty",faculty,"session",session);
    }

    @Transactional
    public Map<String,Object> save(String schoolId,String staffUid,ClassRepresentativeDtos.SaveRequest request) {
        String session=request.session()==null?"":request.session().trim();
        if(session.isBlank()) throw new IllegalArgumentException("Session is required");
        if(request.assignments()!=null) for(ClassRepresentativeDtos.Assignment a:request.assignments()) {
            if(a.classId()==null) continue;
            jdbc.update("DELETE FROM class_Representative WHERE class_id=? AND session=? AND school_id=?",a.classId(),session,schoolId);
            if (a.facultyId()!=null) {
                String facultyStaffUid = jdbc.queryForObject("SELECT TOP 1 staff_u_id FROM tb_facultymaster WHERE school_id=? AND f_id=?", String.class, schoolId, a.facultyId());
                jdbc.update("INSERT INTO class_Representative(class_id,f_id,session,school_id,staff_u_id) VALUES(?,?,?,?,?)",a.classId(),a.facultyId(),session,schoolId,facultyStaffUid);
            }
        }
        return Map.of("message","Class representatives saved","session",session);
    }
}
