package com.edufox.schoolerp.fee.report.report_dailydepositreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Fee/Report/DailyDepositReport.jsx. */
@Repository("page_com_edufox_schoolerp_fee_report_report_dailydepositreport_dailydepositreportrepository_java")
public class DailyDepositReportRepository extends PageArtifactRepository {
    public DailyDepositReportRepository() {
        super("Fee", "report", "DailyDepositReport", "Fee/Report/DailyDepositReport.jsx");
    }
}