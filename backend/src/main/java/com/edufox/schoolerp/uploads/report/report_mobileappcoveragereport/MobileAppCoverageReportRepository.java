package com.edufox.schoolerp.uploads.report.report_mobileappcoveragereport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Uploads/Report/MobileAppCoverageReport.jsx. */
@Repository("page_com_edufox_schoolerp_uploads_report_report_mobileappcoveragereport_mobileappcoveragereportrepository_java")
public class MobileAppCoverageReportRepository extends PageArtifactRepository {
    public MobileAppCoverageReportRepository() {
        super("Uploads", "report", "MobileAppCoverageReport", "Uploads/Report/MobileAppCoverageReport.jsx");
    }
}