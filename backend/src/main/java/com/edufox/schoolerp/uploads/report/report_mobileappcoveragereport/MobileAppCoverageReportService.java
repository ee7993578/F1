package com.edufox.schoolerp.uploads.report.report_mobileappcoveragereport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Uploads/Report/MobileAppCoverageReport.jsx. */
@Service("page_uploads_report_report_mobileappcoveragereport_mobileappcoveragereportservice_java")
public class MobileAppCoverageReportService extends PageArtifactService<MobileAppCoverageReportRepository> {
    public MobileAppCoverageReportService(MobileAppCoverageReportRepository repository) {
        super(repository);
    }
}