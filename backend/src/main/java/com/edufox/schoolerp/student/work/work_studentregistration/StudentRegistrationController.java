package com.edufox.schoolerp.student.work.work_studentregistration;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Work/StudentRegistration.jsx. */
@RestController("page_student_work_work_studentregistration_studentregistrationcontroller_java")
@RequestMapping("/api/page-artifacts/student/work/work_studentregistration")
public class StudentRegistrationController extends PageArtifactController<StudentRegistrationService> {
    public StudentRegistrationController(StudentRegistrationService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}