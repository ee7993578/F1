package com.edufox.schoolerp.student;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class RegistrationOpenRepository {
    private final JdbcTemplate jdbc;

    public List<Map<String,Object>> list(String schoolId, String session) {
        if (session == null || session.isBlank()) {
            return jdbc.queryForList("""
                SELECT r.id, r.class_id AS class_Id, COALESCE(c.class_name,'') AS class_name,
                       r.session, CONVERT(varchar(10),r.open_date,23) AS openDate,
                       CONVERT(varchar(10),r.close_date,23) AS closeDate,
                       r.min_age_year AS minAgeYear, r.min_age_month AS minAgeMonth,
                       r.max_age_year AS maxAgeYear, r.max_age_month AS maxAgeMonth,
                       r.feeAmount, CASE WHEN r.status=1 THEN 'Active' ELSE 'Inactive' END AS status,
                       r.StudentType AS studentType
                FROM registration_openclose r
                LEFT JOIN class_name c ON TRY_CONVERT(int,r.class_id)=c.id AND c.school_id=r.school_id
                WHERE r.school_id=? ORDER BY r.session DESC, r.id DESC
                """, schoolId);
        }
        return jdbc.queryForList("""
            SELECT r.id, r.class_id AS class_Id, COALESCE(c.class_name,'') AS class_name,
                   r.session, CONVERT(varchar(10),r.open_date,23) AS openDate,
                   CONVERT(varchar(10),r.close_date,23) AS closeDate,
                   r.min_age_year AS minAgeYear, r.min_age_month AS minAgeMonth,
                   r.max_age_year AS maxAgeYear, r.max_age_month AS maxAgeMonth,
                   r.feeAmount, CASE WHEN r.status=1 THEN 'Active' ELSE 'Inactive' END AS status,
                   r.StudentType AS studentType
            FROM registration_openclose r
            LEFT JOIN class_name c ON TRY_CONVERT(int,r.class_id)=c.id AND c.school_id=r.school_id
            WHERE r.school_id=? AND r.session=? ORDER BY r.id DESC
            """, schoolId, session);
    }

    @Transactional
    public RegistrationOpenDtos.RegistrationResponse save(String schoolId, String staffUid, RegistrationOpenDtos.RegistrationRequest r) {
        if (text(r.session()).isBlank() || text(r.classId()).isBlank() || text(r.openDate()).isBlank() || text(r.closeDate()).isBlank())
            throw new IllegalArgumentException("Session, class and dates are required");
        boolean active = "1".equals(text(r.status())) || "true".equalsIgnoreCase(text(r.status())) || "Active".equalsIgnoreCase(text(r.status()));
        if (r.id() == null) {
            Integer existing = jdbc.query("SELECT TOP 1 id FROM registration_openclose WHERE school_id=? AND session=? AND class_id=? AND ISNULL(StudentType,'')=ISNULL(?, '')",
                rs -> rs.next() ? rs.getInt(1) : null, schoolId, text(r.session()), text(r.classId()), text(r.studentType()));
            if (existing != null) r = new RegistrationOpenDtos.RegistrationRequest(existing,r.session(),r.classId(),r.openDate(),r.closeDate(),r.minAgeYear(),r.minAgeMonth(),r.maxAgeYear(),r.maxAgeMonth(),r.feeAmount(),r.status(),r.studentType());
        }
        if (r.id() == null) {
            jdbc.update("""
                INSERT INTO registration_openclose
                  (class_id,open_date,close_date,min_age_month,min_age_year,max_age_month,max_age_year,
                   session,school_id,created_by,status,feeAmount,StudentType)
                VALUES (?,TRY_CONVERT(datetime,?,23),TRY_CONVERT(datetime,?,23),?,?,?,?,?,?,?, ?,?,?)
                """, text(r.classId()), text(r.openDate()), text(r.closeDate()), text(r.minAgeMonth()), text(r.minAgeYear()),
                text(r.maxAgeMonth()), text(r.maxAgeYear()), text(r.session()), schoolId, staffUid, active, text(r.feeAmount()), text(r.studentType()));
            Integer id=jdbc.queryForObject("SELECT TOP 1 id FROM registration_openclose WHERE school_id=? ORDER BY id DESC",Integer.class,schoolId);
            return new RegistrationOpenDtos.RegistrationResponse(id,"Registration setting saved");
        }
        jdbc.update("""
            UPDATE registration_openclose SET class_id=?, open_date=TRY_CONVERT(datetime,?,23), close_date=TRY_CONVERT(datetime,?,23),
                min_age_month=?, min_age_year=?, max_age_month=?, max_age_year=?, session=?, status=?, feeAmount=?, StudentType=?,
                modified_by=?, modified_date=GETDATE()
            WHERE id=? AND school_id=?
            """, text(r.classId()), text(r.openDate()), text(r.closeDate()), text(r.minAgeMonth()), text(r.minAgeYear()),
            text(r.maxAgeMonth()), text(r.maxAgeYear()), text(r.session()), active, text(r.feeAmount()), text(r.studentType()),
            staffUid, r.id(), schoolId);
        return new RegistrationOpenDtos.RegistrationResponse(r.id(),"Registration setting updated");
    }

    private static String text(String v){return v==null?"":v.trim();}
}
