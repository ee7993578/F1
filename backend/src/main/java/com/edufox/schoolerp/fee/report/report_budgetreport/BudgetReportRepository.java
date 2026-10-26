package com.edufox.schoolerp.fee.report.report_budgetreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Fee/Report/BudgetReport.jsx. */
@Repository("page_com_edufox_schoolerp_fee_report_report_budgetreport_budgetreportrepository_java")
public class BudgetReportRepository extends PageArtifactRepository {
    public BudgetReportRepository() {
        super("Fee", "report", "BudgetReport", "Fee/Report/BudgetReport.jsx");
    }
}