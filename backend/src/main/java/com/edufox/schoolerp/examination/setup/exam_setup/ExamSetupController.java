package com.edufox.schoolerp.examination.setup.exam_setup;
import com.edufox.schoolerp.examination.report.examination_report.*;
import com.edufox.schoolerp.examination.update.delete_marks.*;
import com.edufox.schoolerp.examination.work.examination_work.*;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/examination/setup")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class ExamSetupController {
    private final ExamSetupService service;

    @GetMapping("/bootstrap")
    public Map<String, Object> bootstrap(Authentication authentication) {
        Claims c = claims(authentication);
        return service.bootstrap(school(c), staff(c));
    }

    @GetMapping("/options")
    public List<Map<String, Object>> options(@RequestParam String type,
                                             @RequestParam Map<String, String> params,
                                             Authentication authentication) {
        Claims c = claims(authentication);
        Map<String, String> clean = new LinkedHashMap<>(params);
        clean.remove("type");
        return service.options(school(c), type, clean);
    }

    @GetMapping("/data")
    public Map<String, Object> data(@RequestParam String page,
                                    @RequestParam Map<String, String> params,
                                    Authentication authentication) {
        Claims c = claims(authentication);
        Map<String, String> clean = new LinkedHashMap<>(params);
        clean.remove("page");
        return service.data(school(c), page, clean);
    }

    @PostMapping("/data")
    public Map<String, Object> save(@RequestParam String page,
                                    @RequestBody Map<String, Object> body,
                                    Authentication authentication) {
        Claims c = claims(authentication);
        service.save(school(c), staff(c), page, body);
        return Map.of("message", "Record Saved Successfully");
    }

    @DeleteMapping("/data")
    public Map<String, Object> delete(@RequestParam String page,
                                      @RequestBody Map<String, Object> body,
                                      Authentication authentication) {
        Claims c = claims(authentication);
        service.delete(school(c), page, body);
        return Map.of("message", "Record deleted successfully");
    }

    private static Claims claims(Authentication a) {
        if (a == null || !(a.getDetails() instanceof Claims c)) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated claims are missing");
        return c;
    }
    private static String school(Claims c) {
        String s = c.get("schoolId", String.class);
        if (s == null || s.isBlank()) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "School claim is missing");
        return s;
    }
    private static String staff(Claims c) { String s = c.get("staffUid", String.class); return s == null ? "" : s; }
}
