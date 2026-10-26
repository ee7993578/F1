package com.edufox.schoolerp.examination.setup.setup_examsetupexact;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Setup/ExamSetupExact.jsx. */
@RestController("page_examination_setup_setup_examsetupexact_examsetupexactcontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/setup/setup_examsetupexact")
public class ExamSetupExactController extends PageArtifactController<ExamSetupExactService> {
    public ExamSetupExactController(ExamSetupExactService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}