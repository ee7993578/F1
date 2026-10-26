package com.edufox.schoolerp.certificate.report.report_tc_report;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Certificates/Report/TC_Report.jsx. */
@Repository("page_com_edufox_schoolerp_certificate_report_report_tc_report_tc_reportrepository_java")
public class TC_ReportRepository extends PageArtifactRepository {
    public TC_ReportRepository() {
        super("Certificates", "report", "TC_Report", "Certificates/Report/TC_Report.jsx");
    }
}