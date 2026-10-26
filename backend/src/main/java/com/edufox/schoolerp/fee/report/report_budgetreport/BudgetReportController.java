package com.edufox.schoolerp.fee.report.report_budgetreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Fee/Report/BudgetReport.jsx. */
@RestController("page_fee_report_report_budgetreport_budgetreportcontroller_java")
@RequestMapping("/api/page-artifacts/fee/report/report_budgetreport")
public class BudgetReportController extends PageArtifactController<BudgetReportService> {
    public BudgetReportController(BudgetReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}