package com.edufox.schoolerp.visitor.work.visitor_approval;

import com.edufox.schoolerp.common.auth.AuthContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController("visitor_approval_controller")
@RequestMapping("/api/visitor")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class VisitorApprovalController {
    private final VisitorApprovalService service;

    private static String school(Authentication authentication) { return AuthContext.value(authentication, "schoolId"); }
    private static Integer userId(Authentication authentication) { return AuthContext.userId(authentication); }
    private static Integer intValue(String value) {
        try { int number = Integer.parseInt(value); return number > 0 ? number : null; }
        catch (Exception ignored) { return null; }
    }

    @GetMapping("/approval")
    public Map<String, Object> approval(@RequestParam(defaultValue = "") String search,
                                        @RequestParam(defaultValue = "") String visitorTypeId,
                                        @RequestParam(defaultValue = "") String departmentId,
                                        @RequestParam(defaultValue = "") String visitDate,
                                        @RequestParam(defaultValue = "Pending") String status,
                                        Authentication authentication) {
        return service.approval(school(authentication), search, intValue(visitorTypeId), intValue(departmentId), visitDate, status);
    }

    @PostMapping("/approval/{id}")
    public Map<String, Object> setApproval(@PathVariable int id, @RequestBody Map<String, Object> body, Authentication authentication) {
        return service.setApproval(school(authentication), userId(authentication), id,
            String.valueOf(body.getOrDefault("status", "")), String.valueOf(body.getOrDefault("reason", "")));
    }
}
