package com.edufox.schoolerp.examination.report.report_toppersreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Report/ToppersReport.jsx. */
@Service("page_examination_report_report_toppersreport_toppersreportservice_java")
public class ToppersReportService extends PageArtifactService<ToppersReportRepository> {
    public ToppersReportService(ToppersReportRepository repository) {
        super(repository);
    }
}