package com.edufox.schoolerp.canteen.report.report_debitcreditbalancereport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Canteen/Report/DebitCreditBalanceReport.jsx. */
@RestController("page_canteen_report_report_debitcreditbalancereport_debitcreditbalancereportcontroller_java")
@RequestMapping("/api/page-artifacts/canteen/report/report_debitcreditbalancereport")
public class DebitCreditBalanceReportController extends PageArtifactController<DebitCreditBalanceReportService> {
    public DebitCreditBalanceReportController(DebitCreditBalanceReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}