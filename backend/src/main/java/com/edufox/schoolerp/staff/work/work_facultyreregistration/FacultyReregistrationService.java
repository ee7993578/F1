package com.edufox.schoolerp.staff.work.work_facultyreregistration;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Staff/Work/FacultyReregistration.jsx. */
@Service("page_staff_work_work_facultyreregistration_facultyreregistrationservice_java")
public class FacultyReregistrationService extends PageArtifactService<FacultyReregistrationRepository> {
    public FacultyReregistrationService(FacultyReregistrationRepository repository) {
        super(repository);
    }
}