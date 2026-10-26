package com.edufox.schoolerp.studentportal.report.report_studentdashboard;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for StundentPages/Report/StudentDashboard.jsx. */
@Repository("page_com_edufox_schoolerp_studentportal_report_report_studentdashboard_studentdashboardrepository_java")
public class StudentDashboardRepository extends PageArtifactRepository {
    public StudentDashboardRepository() {
        super("StundentPages", "report", "StudentDashboard", "StundentPages/Report/StudentDashboard.jsx");
    }
}