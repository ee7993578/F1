package com.edufox.schoolerp.uploads.report.report_mobileappcoveragereport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Uploads/Report/MobileAppCoverageReport.jsx. */
@RestController("page_uploads_report_report_mobileappcoveragereport_mobileappcoveragereportcontroller_java")
@RequestMapping("/api/page-artifacts/uploads/report/report_mobileappcoveragereport")
public class MobileAppCoverageReportController extends PageArtifactController<MobileAppCoverageReportService> {
    public MobileAppCoverageReportController(MobileAppCoverageReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}