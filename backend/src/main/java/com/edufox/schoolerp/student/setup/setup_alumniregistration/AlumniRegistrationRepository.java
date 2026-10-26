package com.edufox.schoolerp.student.setup.setup_alumniregistration;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Setup/AlumniRegistration.jsx. */
@Repository("page_com_edufox_schoolerp_student_setup_setup_alumniregistration_alumniregistrationrepository_java")
public class AlumniRegistrationRepository extends PageArtifactRepository {
    public AlumniRegistrationRepository() {
        super("Student", "setup", "AlumniRegistration", "Student/Setup/AlumniRegistration.jsx");
    }
}