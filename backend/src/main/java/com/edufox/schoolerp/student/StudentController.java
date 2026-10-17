package com.edufox.schoolerp.student;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class StudentController {
    private final StudentService service;

    @GetMapping("/bootstrap")
    public Map<String, Object> bootstrap(Authentication authentication) {
        return service.bootstrap(school(claims(authentication)));
    }

    @GetMapping("/options")
    public Map<String, Object> options(Authentication authentication) {
        return service.bootstrap(school(claims(authentication)));
    }

    @GetMapping("/search")
    public List<Map<String, Object>> search(Authentication authentication,
                                             @RequestParam(required = false) String query,
                                             @RequestParam(required = false) Integer classId,
                                             @RequestParam(required = false) Integer sectionId,
                                             @RequestParam(required = false) String status,
                                             @RequestParam(required = false) String session) {
        return service.search(school(claims(authentication)), query, classId, sectionId, status, session);
    }

    @GetMapping("/academic-options")
    public Map<String, Object> academicOptions(Authentication authentication,
                                                @RequestParam String session,
                                                @RequestParam Integer classId,
                                                @RequestParam(required = false) String termId) {
        return service.academicOptions(school(claims(authentication)), session, classId, termId);
    }

    @GetMapping("/registration-options")
    public Map<String, Object> registrationOptions(Authentication authentication) {
        return service.registrationOptions(school(claims(authentication)));
    }

    @GetMapping("/terms")
    public List<Map<String, Object>> terms() {
        return service.terms();
    }

    @GetMapping("/registration/{rowId}")
    public Map<String, Object> registrationDetail(Authentication authentication, @PathVariable long rowId) {
        return service.registrationDetail(school(claims(authentication)), rowId);
    }

    @GetMapping("/admission-allotment-options")
    public Map<String, Object> admissionAllotmentOptions(Authentication authentication,
                                                          @RequestParam String session,
                                                          @RequestParam Integer classId) {
        return service.admissionAllotmentOptions(school(claims(authentication)), session, classId);
    }

    @GetMapping("/{studentId}")
    public Map<String, Object> findOne(Authentication authentication,
                                        @PathVariable String studentId) {
        return service.findOne(school(claims(authentication)), studentId);
    }

    @GetMapping("/reports/{type}")
    public List<Map<String, Object>> report(Authentication authentication,
                                             @PathVariable String type,
                                             @RequestParam(required = false) String query,
                                             @RequestParam(required = false) Integer classId,
                                             @RequestParam(required = false) Integer sectionId,
                                             @RequestParam(required = false) String status,
                                             @RequestParam(required = false) String session,
                                             @RequestParam(required = false) Integer month,
                                             @RequestParam(required = false) String studentType,
                                             @RequestParam(required = false) String fromDate,
                                             @RequestParam(required = false) String toDate) {
        return service.report(school(claims(authentication)), type, query, classId, sectionId, status, session, month, studentType, fromDate, toDate);
    }

    private static Claims claims(Authentication authentication) {
        if (authentication == null || !(authentication.getDetails() instanceof Claims claims)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated claims are missing");
        }
        return claims;
    }

    private static String school(Claims claims) {
        String value = claims.get("schoolId", String.class);
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "School claim is missing");
        }
        return value;
    }
}
