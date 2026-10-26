package com.edufox.schoolerp.fee.report.report_legacyfeereports;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Fee/Report/LegacyFeeReports.jsx. */
@Repository("page_com_edufox_schoolerp_fee_report_report_legacyfeereports_legacyfeereportsrepository_java")
public class LegacyFeeReportsRepository extends PageArtifactRepository {
    public LegacyFeeReportsRepository() {
        super("Fee", "report", "LegacyFeeReports", "Fee/Report/LegacyFeeReports.jsx");
    }
}