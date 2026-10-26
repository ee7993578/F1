package com.edufox.schoolerp.certificate.report.report_tc_report;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Certificates/Report/TC_Report.jsx. */
@RestController("page_certificate_report_report_tc_report_tc_reportcontroller_java")
@RequestMapping("/api/page-artifacts/certificates/report/report_tc_report")
public class TC_ReportController extends PageArtifactController<TC_ReportService> {
    public TC_ReportController(TC_ReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}