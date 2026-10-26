package com.edufox.schoolerp.student.work.work_registrationredirection;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Work/RegistrationRedirection.jsx. */
@Service("page_student_work_work_registrationredirection_registrationredirectionservice_java")
public class RegistrationRedirectionService extends PageArtifactService<RegistrationRedirectionRepository> {
    public RegistrationRedirectionService(RegistrationRedirectionRepository repository) {
        super(repository);
    }
}