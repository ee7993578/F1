package com.edufox.schoolerp.auth;

import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;

    @PostMapping("/login")
    public AuthDtos.LoginResponse login(@Valid @RequestBody AuthDtos.LoginRequest request) {
        return service.login(request);
    }

    @GetMapping("/me")
    public ResponseEntity<AuthDtos.MeResponse> me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getDetails() instanceof Claims claims)) {
            return ResponseEntity.status(401).build();
        }

        String schoolId = claims.get("schoolId", String.class);
        String staffUid = claims.get("staffUid", String.class);
        if (schoolId == null || schoolId.isBlank() || staffUid == null || staffUid.isBlank()) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(new AuthDtos.MeResponse(new AuthDtos.User(
            schoolId, claims.get("schoolName", String.class), claims.get("campusId", String.class),
            claims.get("username", String.class), staffUid, claims.get("imagePath", String.class),
            claims.get("logoPath", String.class), claims.get("colorScheme", String.class),
            claims.get("typeStatus", String.class), claims.get("dashboard", String.class), null
        )));
    }
}
