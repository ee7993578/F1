package com.edufox.schoolerp.student.update.student_image;
import com.edufox.schoolerp.student.setup.alumni.*;
import com.edufox.schoolerp.student.setup.class_representative.*;
import com.edufox.schoolerp.student.setup.exam_configuration.*;
import com.edufox.schoolerp.student.setup.house_master.*;
import com.edufox.schoolerp.student.setup.registration_open.*;
import com.edufox.schoolerp.student.shared.student_core.*;
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
