package com.edufox.schoolerp.dashboard;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class DashboardController {
    private final DashboardService service;

    @GetMapping("/{type}")
    public Map<String, Object> dashboard(Authentication authentication,
                                         @PathVariable String type,
                                         @RequestParam(required = false) String session,
                                         @RequestParam(required = false, defaultValue = "30") Integer days) {
        Claims claims = claims(authentication);
        String school = claims.get("schoolId", String.class);
        if (school == null || school.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "School claim is missing");
        }
        return service.dashboard(school, type, session, days == null ? 30 : Math.max(1, Math.min(days, 366)));
    }

    private static Claims claims(Authentication authentication) {
        if (authentication == null || !(authentication.getDetails() instanceof Claims claims)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated claims are missing");
        }
        return claims;
    }
}
