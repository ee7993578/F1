package com.edufox.schoolerp.examination.work.work_definemarkspreprimary;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Work/DefineMarksPrePrimary.jsx. */
@RestController("page_examination_work_work_definemarkspreprimary_definemarkspreprimarycontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/work/work_definemarkspreprimary")
public class DefineMarksPrePrimaryController extends PageArtifactController<DefineMarksPrePrimaryService> {
    public DefineMarksPrePrimaryController(DefineMarksPrePrimaryService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}