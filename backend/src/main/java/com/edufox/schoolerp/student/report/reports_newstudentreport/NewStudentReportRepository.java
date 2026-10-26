package com.edufox.schoolerp.student.report.reports_newstudentreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Reports/NewStudentReport.jsx. */
@Repository("page_com_edufox_schoolerp_student_report_reports_newstudentreport_newstudentreportrepository_java")
public class NewStudentReportRepository extends PageArtifactRepository {
    public NewStudentReportRepository() {
        super("Student", "report", "NewStudentReport", "Student/Reports/NewStudentReport.jsx");
    }
}