package com.edufox.schoolerp.student.setup.setup_alumniregistration;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Setup/AlumniRegistration.jsx. */
@RestController("page_student_setup_setup_alumniregistration_alumniregistrationcontroller_java")
@RequestMapping("/api/page-artifacts/student/setup/setup_alumniregistration")
public class AlumniRegistrationController extends PageArtifactController<AlumniRegistrationService> {
    public AlumniRegistrationController(AlumniRegistrationService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}