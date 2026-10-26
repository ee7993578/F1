package com.edufox.schoolerp.studentportal.report.report_paymentgateway;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for StundentPages/Report/PaymentGateway.jsx. */
@Service("page_studentportal_report_report_paymentgateway_paymentgatewayservice_java")
public class PaymentGatewayService extends PageArtifactService<PaymentGatewayRepository> {
    public PaymentGatewayService(PaymentGatewayRepository repository) {
        super(repository);
    }
}