package com.edufox.schoolerp.fee.report.report_feesubreconciliationreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Fee/Report/FeeSubReconciliationReport.jsx. */
@Repository("page_com_edufox_schoolerp_fee_report_report_feesubreconciliationreport_feesubreconciliationreportrepository_java")
public class FeeSubReconciliationReportRepository extends PageArtifactRepository {
    public FeeSubReconciliationReportRepository() {
        super("Fee", "report", "FeeSubReconciliationReport", "Fee/Report/FeeSubReconciliationReport.jsx");
    }
}