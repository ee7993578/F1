package com.edufox.schoolerp.student.setup.setup_alumniregistration;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Setup/AlumniRegistration.jsx. */
@Service("page_student_setup_setup_alumniregistration_alumniregistrationservice_java")
public class AlumniRegistrationService extends PageArtifactService<AlumniRegistrationRepository> {
    public AlumniRegistrationService(AlumniRegistrationRepository repository) {
        super(repository);
    }
}