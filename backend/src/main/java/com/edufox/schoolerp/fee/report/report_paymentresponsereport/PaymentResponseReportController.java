package com.edufox.schoolerp.fee.report.report_paymentresponsereport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Fee/Report/PaymentResponseReport.jsx. */
@RestController("page_fee_report_report_paymentresponsereport_paymentresponsereportcontroller_java")
@RequestMapping("/api/page-artifacts/fee/report/report_paymentresponsereport")
public class PaymentResponseReportController extends PageArtifactController<PaymentResponseReportService> {
    public PaymentResponseReportController(PaymentResponseReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}