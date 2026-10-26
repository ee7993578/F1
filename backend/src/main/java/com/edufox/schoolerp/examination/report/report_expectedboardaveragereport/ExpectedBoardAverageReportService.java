package com.edufox.schoolerp.examination.report.report_expectedboardaveragereport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Report/ExpectedBoardAverageReport.jsx. */
@Service("page_examination_report_report_expectedboardaveragereport_expectedboardaveragereportservice_java")
public class ExpectedBoardAverageReportService extends PageArtifactService<ExpectedBoardAverageReportRepository> {
    public ExpectedBoardAverageReportService(ExpectedBoardAverageReportRepository repository) {
        super(repository);
    }
}