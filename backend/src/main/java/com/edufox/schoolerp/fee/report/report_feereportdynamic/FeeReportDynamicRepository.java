package com.edufox.schoolerp.fee.report.report_feereportdynamic;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Fee/Report/FeeReportDynamic.jsx. */
@Repository("page_com_edufox_schoolerp_fee_report_report_feereportdynamic_feereportdynamicrepository_java")
public class FeeReportDynamicRepository extends PageArtifactRepository {
    public FeeReportDynamicRepository() {
        super("Fee", "report", "FeeReportDynamic", "Fee/Report/FeeReportDynamic.jsx");
    }
}