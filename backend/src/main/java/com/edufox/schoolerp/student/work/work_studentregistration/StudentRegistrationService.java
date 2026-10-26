package com.edufox.schoolerp.student.work.work_studentregistration;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Work/StudentRegistration.jsx. */
@Service("page_student_work_work_studentregistration_studentregistrationservice_java")
public class StudentRegistrationService extends PageArtifactService<StudentRegistrationRepository> {
    public StudentRegistrationService(StudentRegistrationRepository repository) {
        super(repository);
    }
}