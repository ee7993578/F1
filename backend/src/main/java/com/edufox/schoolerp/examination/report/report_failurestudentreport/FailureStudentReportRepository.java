package com.edufox.schoolerp.examination.report.report_failurestudentreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Report/FailureStudentReport.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_report_failurestudentreport_failurestudentreportrepository_java")
public class FailureStudentReportRepository extends PageArtifactRepository {
    public FailureStudentReportRepository() {
        super("ExamMaster", "report", "FailureStudentReport", "ExamMaster/Report/FailureStudentReport.jsx");
    }
}