package com.edufox.schoolerp.fee.report.report_paymentresponsereport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Fee/Report/PaymentResponseReport.jsx. */
@Service("page_fee_report_report_paymentresponsereport_paymentresponsereportservice_java")
public class PaymentResponseReportService extends PageArtifactService<PaymentResponseReportRepository> {
    public PaymentResponseReportService(PaymentResponseReportRepository repository) {
        super(repository);
    }
}