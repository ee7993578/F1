package com.edufox.schoolerp.fee.report.report_feereconciliationreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Fee/Report/FeeReconciliationReport.jsx. */
@RestController("page_fee_report_report_feereconciliationreport_feereconciliationreportcontroller_java")
@RequestMapping("/api/page-artifacts/fee/report/report_feereconciliationreport")
public class FeeReconciliationReportController extends PageArtifactController<FeeReconciliationReportService> {
    public FeeReconciliationReportController(FeeReconciliationReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}