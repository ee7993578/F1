package com.edufox.schoolerp.dashboard.report.alldashboard_admindashboardfees;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Dashboard/AllDashboard/AdminDashboardFees.jsx. */
@Repository("page_com_edufox_schoolerp_dashboard_report_alldashboard_admindashboardfees_admindashboardfeesrepository_java")
public class AdminDashboardFeesRepository extends PageArtifactRepository {
    public AdminDashboardFeesRepository() {
        super("Dashboard", "report", "AdminDashboardFees", "Dashboard/AllDashboard/AdminDashboardFees.jsx");
    }
}