package com.edufox.schoolerp.fee.report.report_dailycollectionreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Fee/Report/DailyCollectionReport.jsx. */
@Repository("page_com_edufox_schoolerp_fee_report_report_dailycollectionreport_dailycollectionreportrepository_java")
public class DailyCollectionReportRepository extends PageArtifactRepository {
    public DailyCollectionReportRepository() {
        super("Fee", "report", "DailyCollectionReport", "Fee/Report/DailyCollectionReport.jsx");
    }
}