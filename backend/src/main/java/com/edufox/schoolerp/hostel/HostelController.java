package com.edufox.schoolerp.hostel;

import com.edufox.schoolerp.config.DatabaseWriteGuard;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hostel")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class HostelController {
    private final HostelService service;
    private final DatabaseWriteGuard writeGuard;
    private static String claim(Authentication authentication, String key) { return String.valueOf(((Claims) authentication.getDetails()).get(key)); }
    private static String s(String value) { return value == null ? "" : value.trim(); }

    @GetMapping("/bootstrap") public Map<String, Object> bootstrap(Authentication a) { return service.bootstrap(claim(a, "schoolId")); }
    @GetMapping("/students") public List<Map<String, Object>> students(@RequestParam(required=false) String classId, @RequestParam(required=false) String admissionNo, @RequestParam(required=false) String session, Authentication a) {
        String school = claim(a, "schoolId"); String selected = s(session); if (selected.isBlank() || "0".equals(selected)) selected = service.bootstrap(school).get("session").toString();
        return service.students(school, selected, s(classId), s(admissionNo));
    }
    @GetMapping("/assignments/report") public List<Map<String, Object>> assignments(@RequestParam(required=false) String classId, @RequestParam(required=false) String search, @RequestParam(required=false) String session, Authentication a) {
        String school = claim(a, "schoolId"); String selected = s(session); if (selected.isBlank() || "0".equals(selected)) selected = service.bootstrap(school).get("session").toString();
        return service.assignments(school, selected, s(classId), s(search));
    }
    @PostMapping("/assignments") public Map<String, Object> saveAssignments(@RequestBody Map<String, Object> body, Authentication a) {
        writeGuard.requireWritable(); String school = claim(a, "schoolId"); String session = s(String.valueOf(body.getOrDefault("session", ""))); if (session.isBlank()) session = service.bootstrap(school).get("session").toString();
        return service.saveAssignments(school, session, claim(a, "staffUid"), body.get("students") instanceof List<?> rows ? rows : List.of());
    }
    @PostMapping("/fees") public Map<String, Object> saveFees(@RequestBody Map<String, Object> body, Authentication a) {
        writeGuard.requireWritable(); String school = claim(a, "schoolId"); String session = s(String.valueOf(body.getOrDefault("session", ""))); if (session.isBlank()) session = service.bootstrap(school).get("session").toString();
        return service.saveFees(school, session, claim(a, "staffUid"), body.get("rows") instanceof List<?> rows ? rows : List.of());
    }
}
