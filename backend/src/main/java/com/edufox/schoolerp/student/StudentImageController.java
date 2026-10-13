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
public class StudentImageController {
    private final StudentImageService service;

    @PutMapping("/{uid}/image")
    public StudentImageDtos.ImageResponse save(
            @PathVariable String uid,
            @RequestBody StudentImageDtos.ImageRequest request,
            Authentication authentication
    ) {
        Claims claims = (Claims) authentication.getDetails();
        return service.save(String.valueOf(claims.get("schoolId")), uid, request);
    }
}