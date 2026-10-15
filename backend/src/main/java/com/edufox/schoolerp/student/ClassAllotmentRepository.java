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
            updated += jdbc.update("""
                UPDATE TB_StudentMaster
                   SET class_id=(SELECT TOP 1 class_id FROM TB_Class WHERE sec_id=? AND school_id=?),
                       sec_id=?, session=?
                 WHERE uid=? AND school_id=?
                """, request.sectionId(), schoolId, request.sectionId(), request.session().trim(), uid.trim(), schoolId);
        }
        return new ClassAllotmentDtos.ClassAllotmentResponse(updated, updated + " student(s) allotted");
    }
}
