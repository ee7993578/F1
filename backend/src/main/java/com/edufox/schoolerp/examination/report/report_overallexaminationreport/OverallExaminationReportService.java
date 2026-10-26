package com.edufox.schoolerp.examination.report.report_overallexaminationreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Report/OverallExaminationReport.jsx. */
@Service("page_examination_report_report_overallexaminationreport_overallexaminationreportservice_java")
public class OverallExaminationReportService extends PageArtifactService<OverallExaminationReportRepository> {
    public OverallExaminationReportService(OverallExaminationReportRepository repository) {
        super(repository);
    }
}