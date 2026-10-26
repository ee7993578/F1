package com.edufox.schoolerp.student.setup.registration_open;
import com.edufox.schoolerp.student.setup.alumni.*;
import com.edufox.schoolerp.student.setup.class_representative.*;
import com.edufox.schoolerp.student.setup.exam_configuration.*;
import com.edufox.schoolerp.student.setup.house_master.*;
import com.edufox.schoolerp.student.shared.student_core.*;
import com.edufox.schoolerp.student.update.student_image.*;
import com.edufox.schoolerp.student.update.student_mutation.*;
import com.edufox.schoolerp.student.update.student_update.*;
import com.edufox.schoolerp.student.work.bulk_admission.*;
import com.edufox.schoolerp.student.work.class_allotment.*;
import com.edufox.schoolerp.student.work.online_registration.*;
import com.edufox.schoolerp.student.work.registration_redirection.*;
import com.edufox.schoolerp.student.work.student_admission.*;
import com.edufox.schoolerp.student.work.student_registration.*;
import com.edufox.schoolerp.student.work.student_work.*;

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
                SELECT r.id, r.class_id AS class_Id, COALESCE(c.displayClassName,c.class_name,'') AS class_name,
                       r.session, CONVERT(varchar(10),r.open_date,23) AS openDate,
                       CONVERT(varchar(10),r.close_date,23) AS closeDate,
                       r.min_age_year AS minAgeYear, r.min_age_month AS minAgeMonth,
                       r.max_age_year AS maxAgeYear, r.max_age_month AS maxAgeMonth,
                       (CASE WHEN NULLIF(LTRIM(RTRIM(r.min_age_year)),'') IS NULL THEN '0' ELSE r.min_age_year END+'y '+
                        CASE WHEN NULLIF(LTRIM(RTRIM(r.min_age_month)),'') IS NULL THEN '0' ELSE r.min_age_month END+'m') AS minAge,
                       (CASE WHEN NULLIF(LTRIM(RTRIM(r.max_age_year)),'') IS NULL THEN '0' ELSE r.max_age_year END+'y '+
                        CASE WHEN NULLIF(LTRIM(RTRIM(r.max_age_month)),'') IS NULL THEN '0' ELSE r.max_age_month END+'m') AS maxAge,
                       r.feeAmount, CASE WHEN r.status=1 THEN 'Active' ELSE 'InActive' END AS status,
                       r.StudentType AS studentType
                FROM registration_openclose r
                LEFT JOIN class_name c ON TRY_CONVERT(int,r.class_id)=c.id AND c.school_id=r.school_id
                WHERE r.school_id=? ORDER BY TRY_CONVERT(int,r.class_id) ASC
                """, schoolId);
        }
        return jdbc.queryForList("""
            SELECT r.id, r.class_id AS class_Id, COALESCE(c.displayClassName,c.class_name,'') AS class_name,
                   r.session, CONVERT(varchar(10),r.open_date,23) AS openDate,
                   CONVERT(varchar(10),r.close_date,23) AS closeDate,
                   r.min_age_year AS minAgeYear, r.min_age_month AS minAgeMonth,
                   r.max_age_year AS maxAgeYear, r.max_age_month AS maxAgeMonth,
                   (CASE WHEN NULLIF(LTRIM(RTRIM(r.min_age_year)),'') IS NULL THEN '0' ELSE r.min_age_year END+'y '+
                    CASE WHEN NULLIF(LTRIM(RTRIM(r.min_age_month)),'') IS NULL THEN '0' ELSE r.min_age_month END+'m') AS minAge,
                   (CASE WHEN NULLIF(LTRIM(RTRIM(r.max_age_year)),'') IS NULL THEN '0' ELSE r.max_age_year END+'y '+
                    CASE WHEN NULLIF(LTRIM(RTRIM(r.max_age_month)),'') IS NULL THEN '0' ELSE r.max_age_month END+'m') AS maxAge,
                   r.feeAmount, CASE WHEN r.status=1 THEN 'Active' ELSE 'InActive' END AS status,
                   r.StudentType AS studentType
            FROM registration_openclose r
            LEFT JOIN class_name c ON TRY_CONVERT(int,r.class_id)=c.id AND c.school_id=r.school_id
            WHERE r.school_id=? AND r.session=? ORDER BY TRY_CONVERT(int,r.class_id) ASC
            """, schoolId, session);
    }

    @Transactional
    public RegistrationOpenDtos.RegistrationResponse save(String schoolId, String staffUid, RegistrationOpenDtos.RegistrationRequest r) {
        if (text(r.session()).isBlank() || text(r.classId()).isBlank() || text(r.openDate()).isBlank() || text(r.closeDate()).isBlank())
            throw new IllegalArgumentException("Session, class and dates are required");
        boolean active = "1".equals(text(r.status())) || "true".equalsIgnoreCase(text(r.status())) || "Active".equalsIgnoreCase(text(r.status()));
        String classId=text(r.classId()), session=text(r.session()), studentType=text(r.studentType());
        Integer existing = classId.equals("17")
            ? jdbc.query("SELECT TOP 1 id FROM registration_openclose WHERE school_id=? AND session=? AND class_id=? AND ISNULL(StudentType,'')=?",
                rs -> rs.next() ? rs.getInt(1) : null, schoolId, session, classId, studentType)
            : jdbc.query("SELECT TOP 1 id FROM registration_openclose WHERE school_id=? AND session=? AND class_id=?",
                rs -> rs.next() ? rs.getInt(1) : null, schoolId, session, classId);
        if (existing == null) {
            jdbc.update("""
                INSERT INTO registration_openclose
                  (class_id,open_date,close_date,min_age_month,min_age_year,max_age_month,max_age_year,
                   session,school_id,created_by,status,feeAmount,StudentType)
                VALUES (?,TRY_CONVERT(datetime,?,23),TRY_CONVERT(datetime,?,23),?,?,?,?,?,?,?, ?,?,?)
                """, classId, text(r.openDate()), text(r.closeDate()), text(r.minAgeMonth()), text(r.minAgeYear()),
                text(r.maxAgeMonth()), text(r.maxAgeYear()), session, schoolId, staffUid, active, text(r.feeAmount()), studentType);
            Integer id=jdbc.queryForObject("SELECT TOP 1 id FROM registration_openclose WHERE school_id=? AND session=? AND class_id=? ORDER BY id DESC",Integer.class,schoolId,session,classId);
            return new RegistrationOpenDtos.RegistrationResponse(id,"Record updated successfully");
        }
        jdbc.update("""
            UPDATE registration_openclose SET class_id=?, open_date=TRY_CONVERT(datetime,?,23), close_date=TRY_CONVERT(datetime,?,23),
                min_age_month=?, min_age_year=?, max_age_month=?, max_age_year=?, session=?, status=?, feeAmount=?, StudentType=?,
                modified_by=?, modified_date=GETDATE()
            WHERE id=? AND school_id=?
            """, classId, text(r.openDate()), text(r.closeDate()), text(r.minAgeMonth()), text(r.minAgeYear()),
            text(r.maxAgeMonth()), text(r.maxAgeYear()), session, active, text(r.feeAmount()), studentType,
            staffUid, existing, schoolId);
        return new RegistrationOpenDtos.RegistrationResponse(existing,"Record updated successfully");
    }

    private static String text(String v){return v==null?"":v.trim();}
}
