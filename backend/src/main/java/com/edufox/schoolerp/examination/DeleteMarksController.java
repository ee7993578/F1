package com.edufox.schoolerp.examination;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/examination/delete-marks")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class DeleteMarksController {
    private final DeleteMarksService service;

    @GetMapping("/bootstrap")
    public Map<String, Object> bootstrap(Authentication a) {
        Claims c = claims(a);
        return service.bootstrap(school(c), currentSession(c));
    }

    @GetMapping("/terms")
    public List<Map<String, Object>> terms(@RequestParam String session, @RequestParam int sectionId, Authentication a) {
        return service.terms(school(claims(a)), session, sectionId);
    }

    @GetMapping("/subjects")
    public List<Map<String, Object>> subjects(@RequestParam String session, @RequestParam int sectionId, Authentication a) {
        return service.subjects(school(claims(a)), session, sectionId);
    }

    @GetMapping("/exam-types")
    public List<Map<String, Object>> examTypes(@RequestParam String session, @RequestParam int sectionId, @RequestParam int termId, Authentication a) {
        return service.examTypes(school(claims(a)), session, sectionId, termId);
    }

    @GetMapping("/rows")
    public Map<String, Object> rows(@RequestParam String session, @RequestParam int sectionId, @RequestParam String marksType, @RequestParam int examTypeId, @RequestParam int subjectId, Authentication a) {
        return service.classRows(school(claims(a)), session, sectionId, marksType, examTypeId, subjectId);
    }

    @GetMapping("/student-search")
    public List<Map<String, Object>> studentSearch(@RequestParam String session, @RequestParam String prefix, Authentication a) {
        return service.studentSearch(school(claims(a)), session, prefix);
    }

    @GetMapping("/student-rows")
    public List<Map<String, Object>> studentRows(@RequestParam String session, @RequestParam String registrationNo, Authentication a) {
        return service.studentRows(school(claims(a)), session, registrationNo);
    }

    @PostMapping("/delete")
    public Map<String, Object> delete(@RequestBody Map<String, Object> body, Authentication a) {
        Claims c = claims(a);
        service.delete(school(c), String.valueOf(body.getOrDefault("session", currentSession(c))), staff(c), rows(body));
        return Map.of("message", "Record Saved Successfully");
    }

    private static List<Map<String, Object>> rows(Map<String, Object> body) {
        Object value = body.get("rows");
        if (!(value instanceof List<?> list)) return List.of();
        return list.stream().filter(Map.class::isInstance).map(item -> (Map<String, Object>) item).toList();
    }
    private static Claims claims(Authentication a) { if (a == null || !(a.getDetails() instanceof Claims c)) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated claims are missing"); return c; }
    private static String school(Claims c) { String value = c.get("schoolId", String.class); if (value == null || value.isBlank()) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "School claim is missing"); return value; }
    private static String staff(Claims c) { return c.get("staffUid", String.class) == null ? "" : c.get("staffUid", String.class); }
    private static String currentSession(Claims c) { return c.get("session", String.class) == null ? "" : c.get("session", String.class); }
}
