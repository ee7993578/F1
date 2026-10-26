package com.edufox.schoolerp.studentportal.report.report_paymentsuccessful;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for StundentPages/Report/PaymentSuccessful.jsx. */
@Service("page_studentportal_report_report_paymentsuccessful_paymentsuccessfulservice_java")
public class PaymentSuccessfulService extends PageArtifactService<PaymentSuccessfulRepository> {
    public PaymentSuccessfulService(PaymentSuccessfulRepository repository) {
        super(repository);
    }
}