package com.edufox.schoolerp.student.update.student_mutation;
import com.edufox.schoolerp.student.setup.alumni.*;
import com.edufox.schoolerp.student.setup.class_representative.*;
import com.edufox.schoolerp.student.setup.exam_configuration.*;
import com.edufox.schoolerp.student.setup.house_master.*;
import com.edufox.schoolerp.student.setup.registration_open.*;
import com.edufox.schoolerp.student.shared.student_core.*;
import com.edufox.schoolerp.student.update.student_image.*;
import com.edufox.schoolerp.student.update.student_update.*;
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
@RequestMapping("/api/students")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class StudentMutationController {
    private final StudentMutationService service;

    @PutMapping("/{uid}")
    public StudentMutationDtos.MutationResponse update(
            @PathVariable String uid,
            @RequestBody StudentMutationDtos.UpdateRequest request,
            Authentication authentication
    ) {
        Claims claims = (Claims) authentication.getDetails();
        return service.update(String.valueOf(claims.get("schoolId")), uid, request);
    }

    @DeleteMapping("/{uid}")
    public StudentMutationDtos.MutationResponse delete(@PathVariable String uid, Authentication authentication) {
        Claims claims = (Claims) authentication.getDetails();
        return service.delete(String.valueOf(claims.get("schoolId")), uid);
    }

    @PostMapping("/withdraw")
    public StudentMutationDtos.MutationResponse updateWithdrawnReason(
            @RequestBody StudentMutationDtos.WithdrawReasonRequest request,
            Authentication authentication) {
        Claims claims = (Claims) authentication.getDetails();
        return service.updateWithdrawnReason(String.valueOf(claims.get("schoolId")), request);
    }
}
