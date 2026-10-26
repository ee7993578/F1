package com.edufox.schoolerp.student.report.reports_studenttypereport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Reports/StudentTypeReport.jsx. */
@Repository("page_com_edufox_schoolerp_student_report_reports_studenttypereport_studenttypereportrepository_java")
public class StudentTypeReportRepository extends PageArtifactRepository {
    public StudentTypeReportRepository() {
        super("Student", "report", "StudentTypeReport", "Student/Reports/StudentTypeReport.jsx");
    }
}