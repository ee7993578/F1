package com.edufox.schoolerp.staff.work.work_facultyregistration;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Staff/Work/FacultyRegistration.jsx. */
@Repository("page_com_edufox_schoolerp_staff_work_work_facultyregistration_facultyregistrationrepository_java")
public class FacultyRegistrationRepository extends PageArtifactRepository {
    public FacultyRegistrationRepository() {
        super("Staff", "work", "FacultyRegistration", "Staff/Work/FacultyRegistration.jsx");
    }
}