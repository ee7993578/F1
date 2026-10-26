package com.edufox.schoolerp.student.setup.setup_registrationopen;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Setup/RegistrationOpen.jsx. */
@Repository("page_com_edufox_schoolerp_student_setup_setup_registrationopen_registrationopenrepository_java")
public class RegistrationOpenRepository extends PageArtifactRepository {
    public RegistrationOpenRepository() {
        super("Student", "setup", "RegistrationOpen", "Student/Setup/RegistrationOpen.jsx");
    }
}