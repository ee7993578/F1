package com.edufox.schoolerp.canteen.report.report_debitcreditbalancereport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Canteen/Report/DebitCreditBalanceReport.jsx. */
@Service("page_canteen_report_report_debitcreditbalancereport_debitcreditbalancereportservice_java")
public class DebitCreditBalanceReportService extends PageArtifactService<DebitCreditBalanceReportRepository> {
    public DebitCreditBalanceReportService(DebitCreditBalanceReportRepository repository) {
        super(repository);
    }
}