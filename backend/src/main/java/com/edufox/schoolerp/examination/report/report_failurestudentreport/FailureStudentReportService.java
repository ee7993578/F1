package com.edufox.schoolerp.examination.report.report_failurestudentreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Report/FailureStudentReport.jsx. */
@Service("page_examination_report_report_failurestudentreport_failurestudentreportservice_java")
public class FailureStudentReportService extends PageArtifactService<FailureStudentReportRepository> {
    public FailureStudentReportService(FailureStudentReportRepository repository) {
        super(repository);
    }
}