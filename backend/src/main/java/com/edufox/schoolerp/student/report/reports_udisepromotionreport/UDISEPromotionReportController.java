package com.edufox.schoolerp.student.report.reports_udisepromotionreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Reports/UDISEPromotionReport.jsx. */
@RestController("page_student_report_reports_udisepromotionreport_udisepromotionreportcontroller_java")
@RequestMapping("/api/page-artifacts/student/report/reports_udisepromotionreport")
public class UDISEPromotionReportController extends PageArtifactController<UDISEPromotionReportService> {
    public UDISEPromotionReportController(UDISEPromotionReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}