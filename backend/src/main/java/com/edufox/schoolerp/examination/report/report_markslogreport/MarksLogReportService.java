package com.edufox.schoolerp.examination.report.report_markslogreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Report/MarksLogReport.jsx. */
@Service("page_examination_report_report_markslogreport_markslogreportservice_java")
public class MarksLogReportService extends PageArtifactService<MarksLogReportRepository> {
    public MarksLogReportService(MarksLogReportRepository repository) {
        super(repository);
    }
}