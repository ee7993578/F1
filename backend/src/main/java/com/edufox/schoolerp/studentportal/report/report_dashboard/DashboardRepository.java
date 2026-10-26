package com.edufox.schoolerp.studentportal.report.report_dashboard;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for StundentPages/Report/Dashboard.jsx. */
@Repository("page_com_edufox_schoolerp_studentportal_report_report_dashboard_dashboardrepository_java")
public class DashboardRepository extends PageArtifactRepository {
    public DashboardRepository() {
        super("StundentPages", "report", "Dashboard", "StundentPages/Report/Dashboard.jsx");
    }
}