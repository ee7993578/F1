package com.edufox.schoolerp.fee.report.report_budgetreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Fee/Report/BudgetReport.jsx. */
@Service("page_fee_report_report_budgetreport_budgetreportservice_java")
public class BudgetReportService extends PageArtifactService<BudgetReportRepository> {
    public BudgetReportService(BudgetReportRepository repository) {
        super(repository);
    }
}