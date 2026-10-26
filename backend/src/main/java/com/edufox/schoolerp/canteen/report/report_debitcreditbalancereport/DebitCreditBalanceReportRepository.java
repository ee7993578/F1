package com.edufox.schoolerp.canteen.report.report_debitcreditbalancereport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Canteen/Report/DebitCreditBalanceReport.jsx. */
@Repository("page_com_edufox_schoolerp_canteen_report_report_debitcreditbalancereport_debitcreditbalancereportrepository_java")
public class DebitCreditBalanceReportRepository extends PageArtifactRepository {
    public DebitCreditBalanceReportRepository() {
        super("Canteen", "report", "DebitCreditBalanceReport", "Canteen/Report/DebitCreditBalanceReport.jsx");
    }
}