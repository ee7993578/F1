package com.edufox.schoolerp.fee.report.report_feereconciliationreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Fee/Report/FeeReconciliationReport.jsx. */
@Service("page_fee_report_report_feereconciliationreport_feereconciliationreportservice_java")
public class FeeReconciliationReportService extends PageArtifactService<FeeReconciliationReportRepository> {
    public FeeReconciliationReportService(FeeReconciliationReportRepository repository) {
        super(repository);
    }
}