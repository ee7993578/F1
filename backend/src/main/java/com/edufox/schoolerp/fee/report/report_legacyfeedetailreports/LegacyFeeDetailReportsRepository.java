package com.edufox.schoolerp.fee.report.report_legacyfeedetailreports;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Fee/Report/LegacyFeeDetailReports.jsx. */
@Repository("page_com_edufox_schoolerp_fee_report_report_legacyfeedetailreports_legacyfeedetailreportsrepository_java")
public class LegacyFeeDetailReportsRepository extends PageArtifactRepository {
    public LegacyFeeDetailReportsRepository() {
        super("Fee", "report", "LegacyFeeDetailReports", "Fee/Report/LegacyFeeDetailReports.jsx");
    }
}