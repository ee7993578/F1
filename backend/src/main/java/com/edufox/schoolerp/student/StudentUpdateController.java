package com.edufox.schoolerp.student;

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
