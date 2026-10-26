package com.edufox.schoolerp.student.work.work_registrationredirection;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Work/RegistrationRedirection.jsx. */
@RestController("page_student_work_work_registrationredirection_registrationredirectioncontroller_java")
@RequestMapping("/api/page-artifacts/student/work/work_registrationredirection")
public class RegistrationRedirectionController extends PageArtifactController<RegistrationRedirectionService> {
    public RegistrationRedirectionController(RegistrationRedirectionService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}