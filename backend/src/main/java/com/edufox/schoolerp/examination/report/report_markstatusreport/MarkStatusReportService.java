package com.edufox.schoolerp.examination.report.report_markstatusreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Report/MarkStatusReport.jsx. */
@Service("page_examination_report_report_markstatusreport_markstatusreportservice_java")
public class MarkStatusReportService extends PageArtifactService<MarkStatusReportRepository> {
    public MarkStatusReportService(MarkStatusReportRepository repository) {
        super(repository);
    }
}