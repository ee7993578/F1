package com.edufox.schoolerp.student.work.online_registration;
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
import com.edufox.schoolerp.student.work.registration_redirection.*;
import com.edufox.schoolerp.student.work.student_admission.*;
import com.edufox.schoolerp.student.work.student_registration.*;
import com.edufox.schoolerp.student.work.student_work.*;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/students/online-registration")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class StudentOnlineRegistrationController {
    private final StudentOnlineRegistrationService service;

    @GetMapping("/{id}")
    public Map<String, Object> detail(Authentication authentication, @PathVariable long id) {
        return service.detail(school(claims(authentication)), id);
    }

    @PostMapping("/confirm")
    public Map<String, Object> confirm(Authentication authentication,
                                       @RequestBody StudentOnlineRegistrationDtos.ConfirmRequest request) {
        Claims c = claims(authentication);
        return service.confirm(school(c), String.valueOf(c.get("staffUid")), request);
    }

    @PostMapping("/status")
    public Map<String, Object> status(Authentication authentication,
                                      @RequestBody StudentOnlineRegistrationDtos.StatusRequest request) {
        return service.updateStatus(school(claims(authentication)), request);
    }

    @PostMapping("/payment")
    public Map<String, Object> payment(Authentication authentication,
                                       @RequestBody StudentOnlineRegistrationDtos.PaymentRequest request) {
        return service.updatePayment(school(claims(authentication)), request);
    }

    private static Claims claims(Authentication authentication) {
        if (authentication == null || !(authentication.getDetails() instanceof Claims claims)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated claims are missing");
        }
        return claims;
    }

    private static String school(Claims claims) {
        String value = claims.get("schoolId", String.class);
        if (value == null || value.isBlank()) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "School claim is missing");
        return value;
    }
}
