package com.edufox.schoolerp.dashboard.report.alldashboard_admindashboard;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Dashboard/AllDashboard/AdminDashboard.jsx. */
@Repository("page_com_edufox_schoolerp_dashboard_report_alldashboard_admindashboard_admindashboardrepository_java")
public class AdminDashboardRepository extends PageArtifactRepository {
    public AdminDashboardRepository() {
        super("Dashboard", "report", "AdminDashboard", "Dashboard/AllDashboard/AdminDashboard.jsx");
    }
}