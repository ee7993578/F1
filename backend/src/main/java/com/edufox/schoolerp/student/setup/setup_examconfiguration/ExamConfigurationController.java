package com.edufox.schoolerp.student.setup.setup_examconfiguration;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Setup/ExamConfiguration.jsx. */
@RestController("page_student_setup_setup_examconfiguration_examconfigurationcontroller_java")
@RequestMapping("/api/page-artifacts/student/setup/setup_examconfiguration")
public class ExamConfigurationController extends PageArtifactController<ExamConfigurationService> {
    public ExamConfigurationController(ExamConfigurationService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}