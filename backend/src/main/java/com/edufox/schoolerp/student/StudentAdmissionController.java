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
public class StudentAdmissionController {
    private final StudentAdmissionService service;

    @PostMapping("/{uid}/admit")
    public StudentAdmissionDtos.AdmissionResponse allot(
            @PathVariable String uid,
            @RequestBody StudentAdmissionDtos.AdmissionRequest request,
            Authentication authentication
    ) {
        Claims claims = (Claims) authentication.getDetails();
        return service.allot(String.valueOf(claims.get("schoolId")), uid, request);
    }
}