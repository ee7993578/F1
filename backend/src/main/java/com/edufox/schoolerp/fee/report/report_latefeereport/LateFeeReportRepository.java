package com.edufox.schoolerp.fee.report.report_latefeereport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Fee/Report/LateFeeReport.jsx. */
@Repository("page_com_edufox_schoolerp_fee_report_report_latefeereport_latefeereportrepository_java")
public class LateFeeReportRepository extends PageArtifactRepository {
    public LateFeeReportRepository() {
        super("Fee", "report", "LateFeeReport", "Fee/Report/LateFeeReport.jsx");
    }
}