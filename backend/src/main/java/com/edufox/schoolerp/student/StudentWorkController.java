package com.edufox.schoolerp.student;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class StudentWorkController {
    private final StudentWorkRepository repository;

    @GetMapping("/admission-allotment")
    public List<Map<String, Object>> admissionAllotment(Authentication authentication,
                                                        @RequestParam String session,
                                                        @RequestParam(required = false) String name,
                                                        @RequestParam(required = false) String formNo) {
        return repository.admissionRows(school(authentication), session, name, formNo);
    }

    @GetMapping("/transferred-admission")
    public List<Map<String, Object>> transferredAdmission(Authentication authentication,
                                                         @RequestParam String session) {
        return repository.transferredRows(school(authentication), session);
    }

    @GetMapping("/class-allotment/students")
    public List<Map<String, Object>> classAllotmentStudents(Authentication authentication,
                                                            @RequestParam String session,
                                                            @RequestParam Integer classId) {
        return repository.newClassAllotmentRows(school(authentication), session, classId);
    }

    @GetMapping("/online-admission")
    public List<Map<String, Object>> onlineAdmission(Authentication authentication,
                                                     @RequestParam String session,
                                                     @RequestParam(required = false) String classIds,
                                                     @RequestParam(required = false) String registrationNo) {
        return repository.onlineAdmissionRows(school(authentication), session, classIds, registrationNo);
    }

    private static String school(Authentication authentication) {
        Claims claims = (Claims) authentication.getDetails();
        return String.valueOf(claims.get("schoolId"));
    }
}
