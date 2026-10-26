package com.edufox.schoolerp.fee.report.report_feeexactreports;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Fee/Report/FeeExactReports.jsx. */
@Repository("page_com_edufox_schoolerp_fee_report_report_feeexactreports_feeexactreportsrepository_java")
public class FeeExactReportsRepository extends PageArtifactRepository {
    public FeeExactReportsRepository() {
        super("Fee", "report", "FeeExactReports", "Fee/Report/FeeExactReports.jsx");
    }
}