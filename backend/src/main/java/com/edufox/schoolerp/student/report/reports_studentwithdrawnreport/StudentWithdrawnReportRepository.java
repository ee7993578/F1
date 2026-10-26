package com.edufox.schoolerp.student.report.reports_studentwithdrawnreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Reports/StudentWithdrawnReport.jsx. */
@Repository("page_com_edufox_schoolerp_student_report_reports_studentwithdrawnreport_studentwithdrawnreportrepository_java")
public class StudentWithdrawnReportRepository extends PageArtifactRepository {
    public StudentWithdrawnReportRepository() {
        super("Student", "report", "StudentWithdrawnReport", "Student/Reports/StudentWithdrawnReport.jsx");
    }
}