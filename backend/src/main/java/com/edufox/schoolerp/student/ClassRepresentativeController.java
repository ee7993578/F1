package com.edufox.schoolerp.student;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/student-settings/class-representatives")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class ClassRepresentativeController {
    private final ClassRepresentativeService service;
    @GetMapping public Map<String,Object> list(@RequestParam String session,Authentication a){Claims c=(Claims)a.getDetails();return service.list(String.valueOf(c.get("schoolId")),session);}
    @PostMapping public Map<String,Object> save(@RequestBody ClassRepresentativeDtos.SaveRequest r,Authentication a){Claims c=(Claims)a.getDetails();return service.save(String.valueOf(c.get("schoolId")),String.valueOf(c.get("staffUid")),r);}
}