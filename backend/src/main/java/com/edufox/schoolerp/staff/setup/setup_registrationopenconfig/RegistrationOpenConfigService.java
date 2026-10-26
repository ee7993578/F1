package com.edufox.schoolerp.staff.setup.setup_registrationopenconfig;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Staff/Setup/RegistrationOpenConfig.jsx. */
@Service("page_staff_setup_setup_registrationopenconfig_registrationopenconfigservice_java")
public class RegistrationOpenConfigService extends PageArtifactService<RegistrationOpenConfigRepository> {
    public RegistrationOpenConfigService(RegistrationOpenConfigRepository repository) {
        super(repository);
    }
}