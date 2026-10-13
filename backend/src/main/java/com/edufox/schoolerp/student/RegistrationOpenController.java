package com.edufox.schoolerp.student;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student-settings/registration-open")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class RegistrationOpenController {
    private final RegistrationOpenService service;
    @GetMapping public List<Map<String,Object>> list(@RequestParam(required=false) String session,Authentication authentication){
        Claims c=(Claims)authentication.getDetails(); return service.list(String.valueOf(c.get("schoolId")),session);
    }
    @PostMapping public RegistrationOpenDtos.RegistrationResponse save(@RequestBody RegistrationOpenDtos.RegistrationRequest r,Authentication authentication){
        Claims c=(Claims)authentication.getDetails(); return service.save(String.valueOf(c.get("schoolId")),String.valueOf(c.get("staffUid")),r);
    }
    @PutMapping("/{id}") public RegistrationOpenDtos.RegistrationResponse update(@PathVariable Integer id,@RequestBody RegistrationOpenDtos.RegistrationRequest r,Authentication authentication){
        Claims c=(Claims)authentication.getDetails(); return service.save(String.valueOf(c.get("schoolId")),String.valueOf(c.get("staffUid")),new RegistrationOpenDtos.RegistrationRequest(id,r.session(),r.classId(),r.openDate(),r.closeDate(),r.minAgeYear(),r.minAgeMonth(),r.maxAgeYear(),r.maxAgeMonth(),r.feeAmount(),r.status(),r.studentType()));
    }
}