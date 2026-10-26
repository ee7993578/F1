package com.edufox.schoolerp.examination.report.report_examreportexact;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Report/ExamReportExact.jsx. */
@RestController("page_examination_report_report_examreportexact_examreportexactcontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/report/report_examreportexact")
public class ExamReportExactController extends PageArtifactController<ExamReportExactService> {
    public ExamReportExactController(ExamReportExactService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}