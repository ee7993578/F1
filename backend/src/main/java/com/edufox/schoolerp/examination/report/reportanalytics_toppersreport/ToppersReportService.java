package com.edufox.schoolerp.examination.report.reportanalytics_toppersreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/ReportAnalytics/ToppersReport.jsx. */
@Service("page_examination_report_reportanalytics_toppersreport_toppersreportservice_java")
public class ToppersReportService extends PageArtifactService<ToppersReportRepository> {
    public ToppersReportService(ToppersReportRepository repository) {
        super(repository);
    }
}