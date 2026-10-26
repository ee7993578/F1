package com.edufox.schoolerp.examination.report.report_examreportexact;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Report/ExamReportExact.jsx. */
@Service("page_examination_report_report_examreportexact_examreportexactservice_java")
public class ExamReportExactService extends PageArtifactService<ExamReportExactRepository> {
    public ExamReportExactService(ExamReportExactRepository repository) {
        super(repository);
    }
}