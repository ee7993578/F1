package com.edufox.schoolerp.fee.report.report_feedefaulterreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Fee/Report/FeeDefaulterReport.jsx. */
@Repository("page_com_edufox_schoolerp_fee_report_report_feedefaulterreport_feedefaulterreportrepository_java")
public class FeeDefaulterReportRepository extends PageArtifactRepository {
    public FeeDefaulterReportRepository() {
        super("Fee", "report", "FeeDefaulterReport", "Fee/Report/FeeDefaulterReport.jsx");
    }
}