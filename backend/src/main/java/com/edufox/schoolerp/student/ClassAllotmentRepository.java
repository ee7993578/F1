package com.edufox.schoolerp.student;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ClassAllotmentRepository {
    private final JdbcTemplate jdbc;

    public ClassAllotmentDtos.ClassAllotmentResponse allot(String schoolId, ClassAllotmentDtos.ClassAllotmentRequest request) {
        if (request.sectionId() == null) throw new IllegalArgumentException("Target class and section are required");
        if (request.session() == null || request.session().isBlank()) throw new IllegalArgumentException("Session is required");
        if (request.studentUids() == null || request.studentUids().isEmpty()) throw new IllegalArgumentException("Select at least one student");

        Integer targetExists = jdbc.queryForObject("SELECT COUNT(*) FROM TB_Class WHERE sec_id=? AND school_id=?", Integer.class, request.sectionId(), schoolId);
        if (targetExists == null || targetExists == 0) throw new IllegalArgumentException("Target class and section were not found");

        int updated = 0;
        for (String uid : request.studentUids()) {
            if (uid == null || uid.isBlank()) continue;
            int changed = jdbc.update("""
                UPDATE TB_StudentMaster
                   SET class_id=(SELECT TOP 1 class_id FROM TB_Class WHERE sec_id=? AND school_id=?),
                       sec_id=?, session=?
                 WHERE uid=? AND school_id=?
                """, request.sectionId(), schoolId, request.sectionId(), request.session().trim(), uid.trim(), schoolId);
            if (changed == 0) {
                Integer nextStudentId = jdbc.queryForObject(
                    "SELECT ISNULL(MAX(stu_id),0)+1 FROM TB_StudentMaster WHERE school_id=?",
                    Integer.class, schoolId);
                int inserted = jdbc.update("""
                    INSERT INTO TB_StudentMaster
                      (uid, stu_id, Registration_NO, House_id, Class_id, sec_id,
                       Admission_date, Registration_date, Withdrawn_date, Withdrawn_reason,
                       status, quota, isnew, activity_id, readmission, school_id, session, stutype)
                    SELECT sr.uid, ?, oi.Registration_NO, '0', sr.class_id, ?,
                           sr.Admission_date, sr.Registration_date, '', '', sr.status,
                           '0', 1, '0', '0', sr.school_id, ?, sr.stutype
                    FROM student_reg sr
                    LEFT JOIN student_other_info oi ON oi.uid=sr.uid AND oi.school_id=sr.school_id
                    WHERE sr.uid=? AND sr.school_id=? AND sr.session=?
                      AND NOT EXISTS (
                          SELECT 1 FROM TB_StudentMaster m
                          WHERE m.uid=sr.uid AND m.school_id=sr.school_id AND m.session=sr.session
                      )
                    """, nextStudentId == null ? 1 : nextStudentId, request.sectionId(), request.session().trim(), uid.trim(), schoolId, request.session().trim());
                changed = inserted;
            }
            updated += changed;
        }
        return new ClassAllotmentDtos.ClassAllotmentResponse(updated, updated + " student(s) allotted");
    }
}
