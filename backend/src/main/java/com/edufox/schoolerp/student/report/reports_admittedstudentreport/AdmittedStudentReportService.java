package com.edufox.schoolerp.student.report.reports_admittedstudentreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Reports/AdmittedStudentReport.jsx. */
@Service("page_student_report_reports_admittedstudentreport_admittedstudentreportservice_java")
public class AdmittedStudentReportService extends PageArtifactService<AdmittedStudentReportRepository> {
    public AdmittedStudentReportService(AdmittedStudentReportRepository repository) {
        super(repository);
    }
}