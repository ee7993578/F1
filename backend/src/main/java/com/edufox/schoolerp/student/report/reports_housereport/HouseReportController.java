package com.edufox.schoolerp.student.report.reports_housereport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Reports/HouseReport.jsx. */
@RestController("page_student_report_reports_housereport_housereportcontroller_java")
@RequestMapping("/api/page-artifacts/student/report/reports_housereport")
public class HouseReportController extends PageArtifactController<HouseReportService> {
    public HouseReportController(HouseReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}