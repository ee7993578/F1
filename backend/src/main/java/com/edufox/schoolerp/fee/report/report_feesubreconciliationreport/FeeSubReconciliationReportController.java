package com.edufox.schoolerp.fee.report.report_feesubreconciliationreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Fee/Report/FeeSubReconciliationReport.jsx. */
@RestController("page_fee_report_report_feesubreconciliationreport_feesubreconciliationreportcontroller_java")
@RequestMapping("/api/page-artifacts/fee/report/report_feesubreconciliationreport")
public class FeeSubReconciliationReportController extends PageArtifactController<FeeSubReconciliationReportService> {
    public FeeSubReconciliationReportController(FeeSubReconciliationReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}