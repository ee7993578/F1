package com.edufox.schoolerp.student.report.reports_siblingreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Reports/SiblingReport.jsx. */
@Service("page_student_report_reports_siblingreport_siblingreportservice_java")
public class SiblingReportService extends PageArtifactService<SiblingReportRepository> {
    public SiblingReportService(SiblingReportRepository repository) {
        super(repository);
    }
}