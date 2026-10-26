package com.edufox.schoolerp.fee.report.report_paymentresponsereport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Fee/Report/PaymentResponseReport.jsx. */
@Repository("page_com_edufox_schoolerp_fee_report_report_paymentresponsereport_paymentresponsereportrepository_java")
public class PaymentResponseReportRepository extends PageArtifactRepository {
    public PaymentResponseReportRepository() {
        super("Fee", "report", "PaymentResponseReport", "Fee/Report/PaymentResponseReport.jsx");
    }
}