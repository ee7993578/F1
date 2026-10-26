package com.edufox.schoolerp.examination.report.report_report_examresultdiscussion;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Report/Report_ExamResultDiscussion.jsx. */
@RestController("page_examination_report_report_report_examresultdiscussion_report_examresultdiscussioncontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/report/report_report_examresultdiscussion")
public class Report_ExamResultDiscussionController extends PageArtifactController<Report_ExamResultDiscussionService> {
    public Report_ExamResultDiscussionController(Report_ExamResultDiscussionService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}