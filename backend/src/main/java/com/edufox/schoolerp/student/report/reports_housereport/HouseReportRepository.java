package com.edufox.schoolerp.student.report.reports_housereport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Reports/HouseReport.jsx. */
@Repository("page_com_edufox_schoolerp_student_report_reports_housereport_housereportrepository_java")
public class HouseReportRepository extends PageArtifactRepository {
    public HouseReportRepository() {
        super("Student", "report", "HouseReport", "Student/Reports/HouseReport.jsx");
    }
}