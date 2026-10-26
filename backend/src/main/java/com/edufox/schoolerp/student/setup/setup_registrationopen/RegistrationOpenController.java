package com.edufox.schoolerp.student.setup.setup_registrationopen;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Setup/RegistrationOpen.jsx. */
@RestController("page_student_setup_setup_registrationopen_registrationopencontroller_java")
@RequestMapping("/api/page-artifacts/student/setup/setup_registrationopen")
public class RegistrationOpenController extends PageArtifactController<RegistrationOpenService> {
    public RegistrationOpenController(RegistrationOpenService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}