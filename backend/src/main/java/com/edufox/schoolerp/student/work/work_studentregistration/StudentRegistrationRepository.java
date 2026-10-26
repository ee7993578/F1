package com.edufox.schoolerp.student.work.work_studentregistration;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Work/StudentRegistration.jsx. */
@Repository("page_com_edufox_schoolerp_student_work_work_studentregistration_studentregistrationrepository_java")
public class StudentRegistrationRepository extends PageArtifactRepository {
    public StudentRegistrationRepository() {
        super("Student", "work", "StudentRegistration", "Student/Work/StudentRegistration.jsx");
    }
}