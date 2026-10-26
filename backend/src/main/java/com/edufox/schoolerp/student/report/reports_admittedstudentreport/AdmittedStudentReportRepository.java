package com.edufox.schoolerp.student.report.reports_admittedstudentreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Reports/AdmittedStudentReport.jsx. */
@Repository("page_com_edufox_schoolerp_student_report_reports_admittedstudentreport_admittedstudentreportrepository_java")
public class AdmittedStudentReportRepository extends PageArtifactRepository {
    public AdmittedStudentReportRepository() {
        super("Student", "report", "AdmittedStudentReport", "Student/Reports/AdmittedStudentReport.jsx");
    }
}