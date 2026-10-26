package com.edufox.schoolerp.visitor.report.visitor_history;

import com.edufox.schoolerp.common.auth.AuthContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController("visitor_history_controller")
@RequestMapping("/api/visitor")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class VisitorHistoryController {
    private final VisitorHistoryService service;

    @GetMapping("/history")
    public Map<String, Object> history(@RequestParam(defaultValue = "") String from,
                                       @RequestParam(defaultValue = "") String to,
                                       @RequestParam(defaultValue = "") String status,
                                       @RequestParam(defaultValue = "") String search,
                                       Authentication authentication) {
        return service.history(AuthContext.value(authentication, "schoolId"), from, to, status, search);
    }

    @GetMapping("/history/{id}")
    public Map<String, Object> details(@PathVariable int id, Authentication authentication) {
        return service.details(AuthContext.value(authentication, "schoolId"), id);
    }
}
