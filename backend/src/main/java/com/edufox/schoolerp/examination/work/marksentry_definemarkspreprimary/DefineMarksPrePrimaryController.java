package com.edufox.schoolerp.examination.work.marksentry_definemarkspreprimary;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/marksEntry/DefineMarksPrePrimary.jsx. */
@RestController("page_examination_work_marksentry_definemarkspreprimary_definemarkspreprimarycontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/work/marksentry_definemarkspreprimary")
public class DefineMarksPrePrimaryController extends PageArtifactController<DefineMarksPrePrimaryService> {
    public DefineMarksPrePrimaryController(DefineMarksPrePrimaryService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}