package com.edufox.schoolerp.examination.report.reportanalytics_subjectreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/ReportAnalytics/SubjectReport.jsx. */
@RestController("page_examination_report_reportanalytics_subjectreport_subjectreportcontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/report/reportanalytics_subjectreport")
public class SubjectReportController extends PageArtifactController<SubjectReportService> {
    public SubjectReportController(SubjectReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}