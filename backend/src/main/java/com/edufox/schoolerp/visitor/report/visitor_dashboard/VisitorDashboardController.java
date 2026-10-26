package com.edufox.schoolerp.visitor.report.visitor_dashboard;

import com.edufox.schoolerp.common.auth.AuthContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController("visitor_dashboard_controller")
@RequestMapping("/api/visitor")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class VisitorDashboardController {
    private final VisitorDashboardService service;

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard(Authentication authentication) {
        return service.dashboard(AuthContext.value(authentication, "schoolId"));
    }

    @GetMapping("/dashboard/appointment")
    public Map<String, Object> lookup(@RequestParam(defaultValue = "") String refNo, Authentication authentication) {
        return service.lookupAppointment(AuthContext.value(authentication, "schoolId"), refNo);
    }
}
