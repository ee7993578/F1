package com.edufox.schoolerp.student.report.reports_strengthreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Reports/StrengthReport.jsx. */
@RestController("page_student_report_reports_strengthreport_strengthreportcontroller_java")
@RequestMapping("/api/page-artifacts/student/report/reports_strengthreport")
public class StrengthReportController extends PageArtifactController<StrengthReportService> {
    public StrengthReportController(StrengthReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}