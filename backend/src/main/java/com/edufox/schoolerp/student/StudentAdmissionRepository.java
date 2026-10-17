package com.edufox.schoolerp.student;

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
                SELECT TOP 1 form_no, class_id, session, Registration_date, stutype, quota
                FROM student_reg WHERE uid=? AND school_id=?
                """, uid, schoolId);
        } catch (EmptyResultDataAccessException ex) {
            throw new IllegalArgumentException("Registration record not found");
        }

        String admissionNo = text(request.admissionNo());
        if (admissionNo.isBlank()) throw new IllegalArgumentException("Admission number is required");
        Integer classId = findClassId(schoolId, request.className());
        if (classId == null) classId = (Integer) registration.get("class_id");
        Integer streamId = resolveStreamId(schoolId, request.session(), request.stream());
        Integer stuId = nextStudentId(schoolId);
        String status = text(request.status()).isBlank() ? "Registered" : text(request.status());

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
                text(request.session()).isBlank() ? String.valueOf(registration.get("session")) : text(request.session()),
                text(request.rollNo()), text(request.feeBookNo()),
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
                text(request.session()).isBlank() ? String.valueOf(registration.get("session")) : text(request.session()),
                text(request.rollNo()), text(request.feeBookNo()),
                streamId == null ? text(request.stream()) : String.valueOf(streamId), text(request.optionalSubject()), uid, schoolId
            );
        }

        jdbc.update("UPDATE student_other_info SET Registration_NO=? WHERE uid=? AND school_id=?", admissionNo, uid, schoolId);
        jdbc.update("UPDATE student_reg SET status='Admitted' WHERE uid=? AND school_id=?", uid, schoolId);
        return new StudentAdmissionDtos.AdmissionResponse(uid, stuId, admissionNo, "Admission allotted");
    }

    private Integer nextStudentId(String schoolId) {
        Integer value = jdbc.queryForObject("SELECT ISNULL(MAX(stu_id),0)+1 FROM TB_StudentMaster WHERE school_id=?", Integer.class, schoolId);
        return value == null ? 1 : value;
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
