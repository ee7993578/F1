package com.edufox.schoolerp.student;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student-settings/alumni")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class AlumniController {
    private final AlumniService service;
    @GetMapping public List<Map<String,Object>> list(Authentication a){Claims c=(Claims)a.getDetails();return service.list(String.valueOf(c.get("schoolId")));}
    @PostMapping public AlumniDtos.AlumniResponse save(@RequestBody AlumniDtos.AlumniRequest r,Authentication a){Claims c=(Claims)a.getDetails();return service.save(String.valueOf(c.get("schoolId")),r);}
}