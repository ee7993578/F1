package com.edufox.schoolerp.examination.work.marksentry_definemarks;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/marksEntry/DefineMarks.jsx. */
@RestController("page_examination_work_marksentry_definemarks_definemarkscontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/work/marksentry_definemarks")
public class DefineMarksController extends PageArtifactController<DefineMarksService> {
    public DefineMarksController(DefineMarksService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}