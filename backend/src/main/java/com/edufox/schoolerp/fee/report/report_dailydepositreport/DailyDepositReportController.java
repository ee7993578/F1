package com.edufox.schoolerp.fee.report.report_dailydepositreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Fee/Report/DailyDepositReport.jsx. */
@RestController("page_fee_report_report_dailydepositreport_dailydepositreportcontroller_java")
@RequestMapping("/api/page-artifacts/fee/report/report_dailydepositreport")
public class DailyDepositReportController extends PageArtifactController<DailyDepositReportService> {
    public DailyDepositReportController(DailyDepositReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}