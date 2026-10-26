package com.edufox.schoolerp.examination.report.reportanalytics_toppersreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/ReportAnalytics/ToppersReport.jsx. */
@RestController("page_examination_report_reportanalytics_toppersreport_toppersreportcontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/report/reportanalytics_toppersreport")
public class ToppersReportController extends PageArtifactController<ToppersReportService> {
    public ToppersReportController(ToppersReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}