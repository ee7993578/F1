package com.edufox.schoolerp.student;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class StudentWriteController {
    private final StudentWriteService service;

    @PostMapping("/registration")
    public StudentWriteDtos.WriteResponse register(Authentication authentication,
                                                    @RequestBody StudentWriteDtos.RegistrationRequest request) {
        Claims claims = authentication != null && authentication.getDetails() instanceof Claims c ? c : null;
        if (claims == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated claims are missing");
        String schoolId = claims.get("schoolId", String.class);
        String staffUid = claims.get("staffUid", String.class);
        if (schoolId == null || schoolId.isBlank() || staffUid == null || staffUid.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "School or staff claim is missing");
        }
        return service.register(schoolId, staffUid, request);
    }
}