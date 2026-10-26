package com.edufox.schoolerp.examination.work.work_definelearnerprofileindicator;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Work/DefineLearnerProfileIndicator.jsx. */
@RestController("page_examination_work_work_definelearnerprofileindicator_definelearnerprofileindicatorcontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/work/work_definelearnerprofileindicator")
public class DefineLearnerProfileIndicatorController extends PageArtifactController<DefineLearnerProfileIndicatorService> {
    public DefineLearnerProfileIndicatorController(DefineLearnerProfileIndicatorService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}