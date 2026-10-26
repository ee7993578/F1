package com.edufox.schoolerp.staff.work.work_facultyregistration;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Staff/Work/FacultyRegistration.jsx. */
@Service("page_staff_work_work_facultyregistration_facultyregistrationservice_java")
public class FacultyRegistrationService extends PageArtifactService<FacultyRegistrationRepository> {
    public FacultyRegistrationService(FacultyRegistrationRepository repository) {
        super(repository);
    }
}