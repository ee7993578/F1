package com.edufox.schoolerp.fee.report.report_dailydepositreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Fee/Report/DailyDepositReport.jsx. */
@Service("page_fee_report_report_dailydepositreport_dailydepositreportservice_java")
public class DailyDepositReportService extends PageArtifactService<DailyDepositReportRepository> {
    public DailyDepositReportService(DailyDepositReportRepository repository) {
        super(repository);
    }
}