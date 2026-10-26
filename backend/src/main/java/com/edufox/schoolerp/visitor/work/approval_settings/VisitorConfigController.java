package com.edufox.schoolerp.visitor.work.approval_settings;

import com.edufox.schoolerp.common.auth.AuthContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController("visitor_config_controller")
@RequestMapping("/api/visitor")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class VisitorConfigController {
    private final VisitorConfigService service;

    private static String school(Authentication authentication) { return AuthContext.value(authentication, "schoolId"); }
    private static Integer userId(Authentication authentication) { return AuthContext.userId(authentication); }

    @GetMapping("/bootstrap")
    public Map<String, Object> bootstrap(Authentication authentication) { return service.bootstrap(school(authentication)); }

    @GetMapping("/config")
    public Map<String, Object> config(Authentication authentication) { return service.config(school(authentication)); }

    @PutMapping("/config")
    public Map<String, Object> saveConfig(@RequestBody Map<String, Object> body, Authentication authentication) {
        return service.saveConfig(school(authentication), userId(authentication), body);
    }
}
