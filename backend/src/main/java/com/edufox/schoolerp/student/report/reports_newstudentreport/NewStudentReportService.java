package com.edufox.schoolerp.student.report.reports_newstudentreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Reports/NewStudentReport.jsx. */
@Service("page_student_report_reports_newstudentreport_newstudentreportservice_java")
public class NewStudentReportService extends PageArtifactService<NewStudentReportRepository> {
    public NewStudentReportService(NewStudentReportRepository repository) {
        super(repository);
    }
}