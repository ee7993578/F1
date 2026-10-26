package com.edufox.schoolerp.examination.work.work_examworkexact;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Work/ExamWorkExact.jsx. */
@RestController("page_examination_work_work_examworkexact_examworkexactcontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/work/work_examworkexact")
public class ExamWorkExactController extends PageArtifactController<ExamWorkExactService> {
    public ExamWorkExactController(ExamWorkExactService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}