package com.edufox.schoolerp.student.report.reports_strengthreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Reports/StrengthReport.jsx. */
@Service("page_student_report_reports_strengthreport_strengthreportservice_java")
public class StrengthReportService extends PageArtifactService<StrengthReportRepository> {
    public StrengthReportService(StrengthReportRepository repository) {
        super(repository);
    }
}