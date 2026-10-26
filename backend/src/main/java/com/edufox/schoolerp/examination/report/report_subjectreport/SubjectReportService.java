package com.edufox.schoolerp.examination.report.report_subjectreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Report/SubjectReport.jsx. */
@Service("page_examination_report_report_subjectreport_subjectreportservice_java")
public class SubjectReportService extends PageArtifactService<SubjectReportRepository> {
    public SubjectReportService(SubjectReportRepository repository) {
        super(repository);
    }
}