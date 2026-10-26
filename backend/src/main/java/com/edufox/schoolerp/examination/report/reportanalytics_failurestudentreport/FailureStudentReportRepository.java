package com.edufox.schoolerp.examination.report.reportanalytics_failurestudentreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/ReportAnalytics/FailureStudentReport.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_reportanalytics_failurestudentreport_failurestudentreportrepository_java")
public class FailureStudentReportRepository extends PageArtifactRepository {
    public FailureStudentReportRepository() {
        super("ExamMaster", "report", "FailureStudentReport", "ExamMaster/ReportAnalytics/FailureStudentReport.jsx");
    }
}