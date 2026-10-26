package com.edufox.schoolerp.student.work.student_work;
import com.edufox.schoolerp.student.setup.alumni.*;
import com.edufox.schoolerp.student.setup.class_representative.*;
import com.edufox.schoolerp.student.setup.exam_configuration.*;
import com.edufox.schoolerp.student.setup.house_master.*;
import com.edufox.schoolerp.student.setup.registration_open.*;
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

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Queries used by the legacy Student/Registration Work pages.
 *
 * These are intentionally kept separate from the general student report
 * queries.  The ASPX pages had different source sets for normal admission,
 * transferred admission and new-class allotment; collapsing them into one
 * generic report was the reason rows were appearing on the wrong page.
 */
@Repository
@RequiredArgsConstructor
public class StudentWorkRepository {
    private final JdbcTemplate jdbc;

    public List<Map<String, Object>> admissionRows(String schoolId, String session,
                                                    String name, String formNo) {
        StringBuilder sql = new StringBuilder("""
            SELECT s.uid AS uid,
                   sr.form_no AS formNo,
                   LTRIM(RTRIM(CONCAT(COALESCE(s.first_name,''),' ',COALESCE(s.mid_name,''),' ',COALESCE(s.last_name,'')))) AS name,
                   s.phone AS phone,
                   (SELECT COUNT(*) FROM student_other_info soi
                    WHERE soi.school_id=s.school_id AND soi.mobile=s.mobile AND NULLIF(soi.mobile,'') IS NOT NULL
                      AND soi.Registration_No IN (SELECT Registration_No FROM TB_StudentMaster
                                                  WHERE school_id=s.school_id AND session=?)) AS numberOfChild,
                   CONVERT(varchar(50), s.dob, 106) AS dob,
                   s.father_name AS fatherName,
                   'Pending' AS status,
                   sr.Registration_date AS registrationDate,
                   sr.class_id AS classId,
                   cn.class_name AS className,
                   COALESCE(s.Registration_NO,'') AS registrationNo,
                   COALESCE(CAST(s.FeeBookNo AS varchar(50)),'') AS feeBookNo,
                   COALESCE(s.transfer_status,'') AS transferStatus,
                   sr.session AS session
            FROM student_other_info s
            JOIN student_reg sr ON sr.uid=s.uid AND sr.school_id=s.school_id
              AND sr.session=? AND sr.status='Admitted'
            LEFT JOIN class_name cn ON cn.school_id=sr.school_id AND cn.id=sr.class_id
            WHERE s.school_id=?
            """);
        // The first parameter belongs to numberOfChild; the rest are added in
        // the same order as the query is built.
        List<Object> args = new java.util.ArrayList<>();
        args.add(session);
        args.add(session);
        args.add(schoolId);
        if (name != null && !name.isBlank()) {
            sql.append(" AND LTRIM(RTRIM(CONCAT(COALESCE(s.first_name,''),' ',COALESCE(s.mid_name,''),' ',COALESCE(s.last_name,'')))) LIKE ?");
            args.add("%" + name.trim() + "%");
        }
        if (formNo != null && !formNo.isBlank()) {
            sql.append(" AND sr.form_no LIKE ?");
            args.add("%" + formNo.trim() + "%");
        }
        sql.append(" ORDER BY TRY_CONVERT(int,sr.form_no) DESC, sr.form_no DESC");
        return jdbc.queryForList(sql.toString(), args.toArray());
    }

    public List<Map<String, Object>> transferredRows(String schoolId, String session) {
        return jdbc.queryForList("""
            SELECT s.uid AS uid,
                   sr.form_no AS formNo,
                   LTRIM(RTRIM(CONCAT(COALESCE(s.first_name,''),' ',COALESCE(s.mid_name,''),' ',COALESCE(s.last_name,'')))) AS name,
                   CONVERT(varchar(50), s.dob, 106) AS dob,
                   s.father_name AS fatherName,
                   'Pending' AS status,
                   COALESCE((SELECT MAX(TRY_CONVERT(int, FeeBookNo))+1 FROM TB_StudentMaster
                             WHERE school_id=? AND session=?),1) AS feeBookNo,
                   sr.Registration_date AS registrationDate,
                   sr.class_id AS classId,
                   cn.class_name AS className,
                   sr.session AS session,
                   COALESCE(s.transfer_status,'') AS transferStatus,
                   COALESCE(s.Registration_NO,'') AS registrationNo
            FROM student_other_info s
            JOIN student_reg sr ON sr.uid=s.uid AND sr.school_id=s.school_id
              AND sr.school_id=? AND sr.session=? AND sr.status='Pending'
            LEFT JOIN class_name cn ON cn.school_id=sr.school_id AND cn.id=sr.class_id
            WHERE s.school_id=? AND NULLIF(LTRIM(RTRIM(s.transfer_status)),'') IS NOT NULL
            ORDER BY TRY_CONVERT(int,sr.form_no), sr.form_no
            """, schoolId, session, schoolId, session, schoolId);
    }

    public List<Map<String, Object>> newClassAllotmentRows(String schoolId, String session, Integer classId) {
        return jdbc.queryForList("""
            SELECT s.uid AS uid,
                   LTRIM(RTRIM(CONCAT(COALESCE(s.first_name,''),' ',COALESCE(s.mid_name,''),' ',COALESCE(s.last_name,'')))) AS name,
                   COALESCE(s.Registration_NO,'') AS registrationNo,
                   s.father_name AS fatherName,
                   sr.class_id AS classId,
                   cn.class_name AS className,
                   sr.session AS session
            FROM student_other_info s
            JOIN student_reg sr ON sr.uid=s.uid AND sr.school_id=s.school_id
              AND sr.school_id=? AND sr.session=? AND sr.status='Registered'
            LEFT JOIN class_name cn ON cn.school_id=sr.school_id AND cn.id=sr.class_id
            WHERE s.school_id=? AND sr.class_id=?
              AND NOT EXISTS (SELECT 1 FROM TB_StudentMaster m
                              WHERE m.school_id=s.school_id AND m.session=? AND m.uid=s.uid)
            ORDER BY name, s.uid
            """, schoolId, session, schoolId, classId, session);
    }

    /** Direct equivalent of getStudentForAdmissonConfirm used by the bulk page. */
    public List<Map<String, Object>> onlineAdmissionRows(String schoolId, String session,
                                                         String classIds, String registrationNo) {
        StringBuilder sql = new StringBuilder("""
            SELECT osr.uid AS uid, osr.formNo AS formNo, osr.regPhone AS regPhone,
                   LTRIM(RTRIM(CONCAT(COALESCE(osr.firstName,''),' ',COALESCE(osr.midName,''),' ',COALESCE(osr.lastName,'')))) AS name,
                   CASE WHEN osr.session='2025 - 2026' THEN c.class_name ELSE c.displayClassName END AS className,
                   COALESCE(osr.fatherName,'') AS fatherName, COALESCE(osr.motherName,'') AS motherName,
                   osr.session AS session, osr.registrationNO AS registrationNo,
                   osr.admissionClass AS classId, osr.AdmissionStatus AS admissionStatus,
                   osr.formStatus AS formStatus, osr.paymentStatus AS paymentStatus,
                   osr.paymentAmount AS paymentAmount, osr.paymentDate AS paymentDate,
                   osr.reveiptNo AS receiptNo
            FROM onlinestudentRegistration osr
            LEFT JOIN class_name c ON c.school_id=osr.school_id AND c.id=osr.admissionClass
            WHERE osr.school_id=? AND osr.session=?
              AND osr.AdmissionStatus='Accept' AND osr.formStatus='completed'
            """);
        List<Object> args = new java.util.ArrayList<>();
        args.add(schoolId); args.add(session);
        if (classIds != null && !classIds.isBlank() && !"0".equals(classIds.trim())) {
            List<String> values = java.util.Arrays.stream(classIds.split(","))
                .map(String::trim).filter(v -> v.matches("\\d+")).toList();
            if (!values.isEmpty()) {
                sql.append(" AND osr.admissionClass IN (")
                    .append(String.join(",", java.util.Collections.nCopies(values.size(), "?")))
                    .append(")");
                values.forEach(v -> args.add(Integer.valueOf(v)));
            }
        }
        if (registrationNo != null && !registrationNo.isBlank()) {
            sql.append(" AND osr.registrationNO=?");
            args.add(registrationNo.trim());
        }
        sql.append(" ORDER BY TRY_CONVERT(int,osr.formNo)");
        return jdbc.queryForList(sql.toString(), args.toArray());
    }
}
