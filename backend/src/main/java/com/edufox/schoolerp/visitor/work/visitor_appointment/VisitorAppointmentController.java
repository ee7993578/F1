package com.edufox.schoolerp.visitor.work.visitor_appointment;

import com.edufox.schoolerp.common.auth.AuthContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController("visitor_appointment_controller")
@RequestMapping("/api/visitor")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class VisitorAppointmentController {
    private final VisitorAppointmentService service;

    private static String school(Authentication authentication) { return AuthContext.value(authentication, "schoolId"); }
    private static Integer userId(Authentication authentication) { return AuthContext.userId(authentication); }

    @GetMapping("/visitors/search")
    public Map<String, Object> search(@RequestParam(defaultValue = "") String term, Authentication authentication) {
        return Map.of("rows", service.search(school(authentication), term));
    }

    @GetMapping("/appointments")
    public Map<String, Object> appointments(@RequestParam(defaultValue = "false") boolean today, Authentication authentication) {
        return service.appointments(school(authentication), today);
    }

    @GetMapping("/appointments/{id}")
    public Map<String, Object> appointment(@PathVariable int id, Authentication authentication) {
        return service.appointment(school(authentication), id);
    }

    @PostMapping("/appointments")
    public Map<String, Object> save(@RequestBody Map<String, Object> body, Authentication authentication) {
        return service.save(school(authentication), userId(authentication), body, null);
    }

    @PutMapping("/appointments/{id}")
    public Map<String, Object> update(@PathVariable int id, @RequestBody Map<String, Object> body, Authentication authentication) {
        return service.save(school(authentication), userId(authentication), body, id);
    }

    @PostMapping("/appointments/{id}/cancel")
    public Map<String, Object> cancel(@PathVariable int id, @RequestBody Map<String, Object> body, Authentication authentication) {
        return service.cancel(school(authentication), id, String.valueOf(body.getOrDefault("reason", "")));
    }
}
