package com.edufox.schoolerp.examination.report.greensheet_expectedschoolaveragereport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Greensheet/ExpectedSchoolAverageReport.jsx. */
@Service("page_examination_report_greensheet_expectedschoolaveragereport_expectedschoolaveragereportservice_java")
public class ExpectedSchoolAverageReportService extends PageArtifactService<ExpectedSchoolAverageReportRepository> {
    public ExpectedSchoolAverageReportService(ExpectedSchoolAverageReportRepository repository) {
        super(repository);
    }
}