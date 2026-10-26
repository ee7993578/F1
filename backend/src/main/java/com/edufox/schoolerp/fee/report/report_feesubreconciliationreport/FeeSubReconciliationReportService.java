package com.edufox.schoolerp.fee.report.report_feesubreconciliationreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Fee/Report/FeeSubReconciliationReport.jsx. */
@Service("page_fee_report_report_feesubreconciliationreport_feesubreconciliationreportservice_java")
public class FeeSubReconciliationReportService extends PageArtifactService<FeeSubReconciliationReportRepository> {
    public FeeSubReconciliationReportService(FeeSubReconciliationReportRepository repository) {
        super(repository);
    }
}