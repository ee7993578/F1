package com.edufox.schoolerp.examination.report.reportanalytics_subjectreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/ReportAnalytics/SubjectReport.jsx. */
@Service("page_examination_report_reportanalytics_subjectreport_subjectreportservice_java")
public class SubjectReportService extends PageArtifactService<SubjectReportRepository> {
    public SubjectReportService(SubjectReportRepository repository) {
        super(repository);
    }
}