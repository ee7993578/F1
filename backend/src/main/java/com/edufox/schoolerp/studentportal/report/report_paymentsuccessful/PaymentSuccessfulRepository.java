package com.edufox.schoolerp.studentportal.report.report_paymentsuccessful;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for StundentPages/Report/PaymentSuccessful.jsx. */
@Repository("page_com_edufox_schoolerp_studentportal_report_report_paymentsuccessful_paymentsuccessfulrepository_java")
public class PaymentSuccessfulRepository extends PageArtifactRepository {
    public PaymentSuccessfulRepository() {
        super("StundentPages", "report", "PaymentSuccessful", "StundentPages/Report/PaymentSuccessful.jsx");
    }
}