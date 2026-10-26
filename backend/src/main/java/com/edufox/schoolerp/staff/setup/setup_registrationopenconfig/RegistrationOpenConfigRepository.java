package com.edufox.schoolerp.staff.setup.setup_registrationopenconfig;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Staff/Setup/RegistrationOpenConfig.jsx. */
@Repository("page_com_edufox_schoolerp_staff_setup_setup_registrationopenconfig_registrationopenconfigrepository_java")
public class RegistrationOpenConfigRepository extends PageArtifactRepository {
    public RegistrationOpenConfigRepository() {
        super("Staff", "setup", "RegistrationOpenConfig", "Staff/Setup/RegistrationOpenConfig.jsx");
    }
}