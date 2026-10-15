package com.edufox.schoolerp.student;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class ClassAllotmentController {
    private final ClassAllotmentService service;

    @PutMapping("/class-allotment")
    public ClassAllotmentDtos.ClassAllotmentResponse allot(
            @RequestBody ClassAllotmentDtos.ClassAllotmentRequest request,
            Authentication authentication
    ) {
        Claims claims = (Claims) authentication.getDetails();
        return service.allot(String.valueOf(claims.get("schoolId")), request);
    }
}
