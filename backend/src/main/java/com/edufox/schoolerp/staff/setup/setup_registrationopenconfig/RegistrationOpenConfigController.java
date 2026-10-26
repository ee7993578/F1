package com.edufox.schoolerp.staff.setup.setup_registrationopenconfig;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Staff/Setup/RegistrationOpenConfig.jsx. */
@RestController("page_staff_setup_setup_registrationopenconfig_registrationopenconfigcontroller_java")
@RequestMapping("/api/page-artifacts/staff/setup/setup_registrationopenconfig")
public class RegistrationOpenConfigController extends PageArtifactController<RegistrationOpenConfigService> {
    public RegistrationOpenConfigController(RegistrationOpenConfigService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}