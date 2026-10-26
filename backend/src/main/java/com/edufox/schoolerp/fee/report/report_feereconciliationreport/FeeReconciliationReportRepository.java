package com.edufox.schoolerp.fee.report.report_feereconciliationreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Fee/Report/FeeReconciliationReport.jsx. */
@Repository("page_com_edufox_schoolerp_fee_report_report_feereconciliationreport_feereconciliationreportrepository_java")
public class FeeReconciliationReportRepository extends PageArtifactRepository {
    public FeeReconciliationReportRepository() {
        super("Fee", "report", "FeeReconciliationReport", "Fee/Report/FeeReconciliationReport.jsx");
    }
}