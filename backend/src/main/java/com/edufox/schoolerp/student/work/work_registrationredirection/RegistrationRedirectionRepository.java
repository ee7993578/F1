package com.edufox.schoolerp.student.work.work_registrationredirection;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Work/RegistrationRedirection.jsx. */
@Repository("page_com_edufox_schoolerp_student_work_work_registrationredirection_registrationredirectionrepository_java")
public class RegistrationRedirectionRepository extends PageArtifactRepository {
    public RegistrationRedirectionRepository() {
        super("Student", "work", "RegistrationRedirection", "Student/Work/RegistrationRedirection.jsx");
    }
}