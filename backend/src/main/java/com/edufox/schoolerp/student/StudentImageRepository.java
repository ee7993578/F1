package com.edufox.schoolerp.student;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class StudentImageRepository {
    private final JdbcTemplate jdbc;

    @Transactional
    public StudentImageDtos.ImageResponse save(String schoolId, String uid, StudentImageDtos.ImageRequest request) {
        String data = request.imageData() == null ? "" : request.imageData().trim();
        if (data.isBlank() || data.length() > 4_000_000) throw new IllegalArgumentException("Invalid or oversized image");
        String column = switch (request.type() == null ? "student" : request.type().toLowerCase()) {
            case "father" -> "fatherimage";
            case "mother" -> "motherimage";
            default -> "studentimage";
        };
        jdbc.update("UPDATE student_other_info SET " + column + "=? WHERE uid=? AND school_id=?", data, uid, schoolId);
        return new StudentImageDtos.ImageResponse(uid, "Student image saved");
    }
}
