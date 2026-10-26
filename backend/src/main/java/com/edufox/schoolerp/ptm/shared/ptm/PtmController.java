package com.edufox.schoolerp.ptm.shared.ptm;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ptm")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class PtmController {
    private final PtmService service;

    private static Claims claims(Authentication authentication) { return (Claims) authentication.getDetails(); }
    private static String claim(Authentication authentication, String key) { return String.valueOf(claims(authentication).getOrDefault(key, "")); }

    @GetMapping("/bootstrap")
    public Map<String,Object> bootstrap(@RequestParam(defaultValue="") String session, Authentication authentication) {
        return service.bootstrap(claim(authentication,"schoolId"), claim(authentication,"staffUid"), session);
    }

    @GetMapping("/master/{id}")
    public Map<String,Object> edit(@PathVariable int id, Authentication authentication) {
        return service.edit(claim(authentication,"schoolId"), id);
    }

    @PostMapping("/master")
    public Map<String,Object> create(@RequestBody Map<String,Object> body, Authentication authentication) {
        return service.save(claim(authentication,"schoolId"), claim(authentication,"staffUid"), body, null);
    }

    @PutMapping("/master/{id}")
    public Map<String,Object> update(@PathVariable int id, @RequestBody Map<String,Object> body, Authentication authentication) {
        return service.save(claim(authentication,"schoolId"), claim(authentication,"staffUid"), body, id);
    }

    @GetMapping("/reports/all")
    public Map<String,Object> allReport(@RequestParam(defaultValue="") String session, @RequestParam(defaultValue="") String status,
                                        @RequestParam(defaultValue="") String search, Authentication authentication) {
        return service.allReport(claim(authentication,"schoolId"), session, status, search);
    }

    @GetMapping("/reports/faculty")
    public Map<String,Object> facultyReport(@RequestParam(defaultValue="") String session, Authentication authentication) {
        return service.facultyReport(claim(authentication,"schoolId"), session);
    }

    @GetMapping("/dashboard")
    public Map<String,Object> dashboard(@RequestParam(defaultValue="") String session, Authentication authentication) {
        return service.dashboard(claim(authentication,"schoolId"), session);
    }
}
