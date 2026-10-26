package com.edufox.schoolerp.student.work.student_admission;
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
import com.edufox.schoolerp.student.work.student_registration.*;
import com.edufox.schoolerp.student.work.student_work.*;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Repository
@RequiredArgsConstructor
public class StudentAdmissionRepository {
    private final JdbcTemplate jdbc;

    @Transactional
    public StudentAdmissionDtos.AdmissionResponse allot(String schoolId, String uid, StudentAdmissionDtos.AdmissionRequest request) {
        Map<String, Object> registration;
        try {
            registration = jdbc.queryForMap("""
                SELECT TOP 1 form_no, class_id, session, Registration_date, stutype
                FROM student_reg WHERE uid=? AND school_id=?
                ORDER BY row_id DESC
                """, uid, schoolId);
        } catch (EmptyResultDataAccessException ex) {
            throw new IllegalArgumentException("Registration record not found");
        }

        String admissionNo = text(request.admissionNo());
        if (admissionNo.isBlank()) throw new IllegalArgumentException("Admission number is required");
        Integer duplicate = jdbc.queryForObject(
            "SELECT COUNT(*) FROM student_other_info WHERE school_id=? AND Registration_NO=? AND uid<>?",
            Integer.class, schoolId, admissionNo, uid);
        if (duplicate != null && duplicate > 0) throw new IllegalArgumentException("Admission number already exists");

        Integer classId = request.sectionId() == null ? null : jdbc.query(
            "SELECT TOP 1 class_id FROM TB_Class WHERE school_id=? AND sec_id=?",
            rs -> rs.next() ? rs.getInt(1) : null, schoolId, request.sectionId());
        if (classId == null) classId = findClassId(schoolId, request.className());
        if (classId == null) classId = (Integer) registration.get("class_id");
        Integer streamId = resolveStreamId(schoolId, request.session(), request.stream());
        Integer stuId = nextStudentId(schoolId);
        String status = text(request.status()).isBlank() ? "Registered" : text(request.status());
        String session = text(request.session()).isBlank() ? String.valueOf(registration.get("session")) : text(request.session());
        String feeBookNo = text(request.feeBookNo());
        if (feeBookNo.isBlank()) feeBookNo = nextFeeBookNo(schoolId, session);

        Integer existing = jdbc.query(
            "SELECT TOP 1 stu_id FROM TB_StudentMaster WHERE uid=? AND school_id=?",
            rs -> rs.next() ? rs.getInt(1) : null, uid, schoolId
        );
        if (existing == null) {
            jdbc.update("""
                INSERT INTO TB_StudentMaster
                  (uid, stu_id, Registration_NO, Class_id, sec_id, Admission_date,
                   Registration_date, status, stutype, school_id, session, RollNo, FeeBookNo,
                   Stream_id, Optional_sub)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                uid, stuId, admissionNo, classId, request.sectionId(),
                text(request.admissionDate()), String.valueOf(registration.get("Registration_date")),
                status, String.valueOf(registration.get("stutype")), schoolId,
                session,
                text(request.rollNo()), feeBookNo,
                streamId == null ? text(request.stream()) : String.valueOf(streamId), text(request.optionalSubject())
            );
        } else {
            stuId = existing;
            jdbc.update("""
                UPDATE TB_StudentMaster SET Registration_NO=?, Class_id=?, sec_id=?,
                    Admission_date=?, status=?, session=?, RollNo=?, FeeBookNo=?, Stream_id=?, Optional_sub=?
                WHERE uid=? AND school_id=?
                """,
                admissionNo, classId, request.sectionId(), text(request.admissionDate()), status,
                session,
                text(request.rollNo()), feeBookNo,
                streamId == null ? text(request.stream()) : String.valueOf(streamId), text(request.optionalSubject()), uid, schoolId
            );
        }

        jdbc.update("""
            UPDATE student_other_info
               SET Registration_NO=?, FeeBookNo=?, att_date=CONVERT(varchar(50),CAST(GETDATE() AS date),100),
                   login_status=1, Student_id=?, Password=?
             WHERE uid=? AND school_id=?
            """, admissionNo, feeBookNo, admissionNo, admissionNo, uid, schoolId);
        jdbc.update("UPDATE student_reg SET status='Registered', Admission_date=? WHERE uid=? AND school_id=? AND session=?",
            text(request.admissionDate()), uid, schoolId, session);
        return new StudentAdmissionDtos.AdmissionResponse(uid, stuId, admissionNo, "Admission allotted");
    }

    private Integer nextStudentId(String schoolId) {
        Integer value = jdbc.queryForObject("SELECT ISNULL(MAX(stu_id),0)+1 FROM TB_StudentMaster WHERE school_id=?", Integer.class, schoolId);
        return value == null ? 1 : value;
    }

    private String nextFeeBookNo(String schoolId, String session) {
        Integer value = jdbc.queryForObject("""
            SELECT ISNULL(MAX(TRY_CONVERT(int, NULLIF(LTRIM(RTRIM(FeeBookNo)),''))),0)+1
            FROM TB_StudentMaster WHERE school_id=? AND session=?
            """, Integer.class, schoolId, session);
        return String.valueOf(value == null ? 1 : value);
    }

    private Integer findClassId(String schoolId, String className) {
        if (className == null || className.isBlank()) return null;
        try {
            return jdbc.queryForObject("SELECT TOP 1 id FROM class_name WHERE school_id=? AND class_name=? ORDER BY id", Integer.class, schoolId, className);
        } catch (EmptyResultDataAccessException ex) {
            try { return Integer.valueOf(className); } catch (NumberFormatException ignored) { return null; }
        }
    }

    private Integer resolveStreamId(String schoolId, String session, String stream) {
        if (text(stream).isBlank()) return null;
        try { return Integer.valueOf(text(stream)); }
        catch (NumberFormatException ignored) {
            return jdbc.query("""
                SELECT TOP 1 sm.stream_id
                FROM tb_stream_mapping sm JOIN tb_stream s ON s.stream_id=sm.stream_id
                WHERE sm.school_id=? AND sm.session=? AND LOWER(s.stream_name)=LOWER(?)
                """, rs -> rs.next() ? rs.getInt(1) : null, schoolId, session, text(stream));
        }
    }

    private static String text(String value) { return value == null ? "" : value.trim(); }
}
