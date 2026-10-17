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
public class RegistrationRedirectionController {
    private final RegistrationRedirectionService service;

    @PostMapping("/registration-redirection")
    public RegistrationRedirectionDtos.Response redirect(
        @RequestBody RegistrationRedirectionDtos.Request request, Authentication authentication) {
        Claims claims = (Claims) authentication.getDetails();
        return service.redirect(String.valueOf(claims.get("schoolId")), String.valueOf(claims.get("staffUid")), request);
    }
}
