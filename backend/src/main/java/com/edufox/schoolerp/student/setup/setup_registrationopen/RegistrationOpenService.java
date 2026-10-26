package com.edufox.schoolerp.student.setup.setup_registrationopen;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Setup/RegistrationOpen.jsx. */
@Service("page_student_setup_setup_registrationopen_registrationopenservice_java")
public class RegistrationOpenService extends PageArtifactService<RegistrationOpenRepository> {
    public RegistrationOpenService(RegistrationOpenRepository repository) {
        super(repository);
    }
}