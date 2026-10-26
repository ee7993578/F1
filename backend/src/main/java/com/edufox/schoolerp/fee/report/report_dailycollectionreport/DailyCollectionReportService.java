package com.edufox.schoolerp.fee.report.report_dailycollectionreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Fee/Report/DailyCollectionReport.jsx. */
@Service("page_fee_report_report_dailycollectionreport_dailycollectionreportservice_java")
public class DailyCollectionReportService extends PageArtifactService<DailyCollectionReportRepository> {
    public DailyCollectionReportService(DailyCollectionReportRepository repository) {
        super(repository);
    }
}