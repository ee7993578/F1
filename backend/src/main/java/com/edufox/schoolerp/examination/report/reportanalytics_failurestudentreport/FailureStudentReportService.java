package com.edufox.schoolerp.examination.report.reportanalytics_failurestudentreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/ReportAnalytics/FailureStudentReport.jsx. */
@Service("page_examination_report_reportanalytics_failurestudentreport_failurestudentreportservice_java")
public class FailureStudentReportService extends PageArtifactService<FailureStudentReportRepository> {
    public FailureStudentReportService(FailureStudentReportRepository repository) {
        super(repository);
    }
}