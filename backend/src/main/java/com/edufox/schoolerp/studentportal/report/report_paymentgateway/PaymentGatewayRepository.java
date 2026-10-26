package com.edufox.schoolerp.studentportal.report.report_paymentgateway;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for StundentPages/Report/PaymentGateway.jsx. */
@Repository("page_com_edufox_schoolerp_studentportal_report_report_paymentgateway_paymentgatewayrepository_java")
public class PaymentGatewayRepository extends PageArtifactRepository {
    public PaymentGatewayRepository() {
        super("StundentPages", "report", "PaymentGateway", "StundentPages/Report/PaymentGateway.jsx");
    }
}