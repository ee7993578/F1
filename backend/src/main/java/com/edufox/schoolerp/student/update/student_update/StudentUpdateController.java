package com.edufox.schoolerp.student.update.student_update;
import com.edufox.schoolerp.student.setup.alumni.*;
import com.edufox.schoolerp.student.setup.class_representative.*;
import com.edufox.schoolerp.student.setup.exam_configuration.*;
import com.edufox.schoolerp.student.setup.house_master.*;
import com.edufox.schoolerp.student.setup.registration_open.*;
import com.edufox.schoolerp.student.shared.student_core.*;
import com.edufox.schoolerp.student.update.student_image.*;
import com.edufox.schoolerp.student.update.student_mutation.*;
import com.edufox.schoolerp.student.work.bulk_admission.*;
import com.edufox.schoolerp.student.work.class_allotment.*;
import com.edufox.schoolerp.student.work.online_registration.*;
import com.edufox.schoolerp.student.work.registration_redirection.*;
import com.edufox.schoolerp.student.work.student_admission.*;
import com.edufox.schoolerp.student.work.student_registration.*;
import com.edufox.schoolerp.student.work.student_work.*;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students/update")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class StudentUpdateController {
    private final StudentUpdateService service;

    @PostMapping("/move")
    public StudentUpdateDtos.MoveResponse move(@RequestBody StudentUpdateDtos.MoveRequest request,
                                               Authentication authentication) {
        Claims claims = (Claims) authentication.getDetails();
        return service.move(String.valueOf(claims.get("schoolId")), request);
    }

    @GetMapping("/schools")
    public Object schools(Authentication authentication) {
        Claims claims = (Claims) authentication.getDetails();
        return service.schools(String.valueOf(claims.get("schoolId")));
    }

    @GetMapping("/options")
    public Object options(@RequestParam(required = false) String schoolId, Authentication authentication) {
        Claims claims = (Claims) authentication.getDetails();
        return service.options(String.valueOf(claims.get("schoolId")), schoolId);
    }
}
