package com.edufox.schoolerp.dashboard.report.alldashboard_managementdashboard;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Dashboard/AllDashboard/ManagementDashboard.jsx. */
@Repository("page_com_edufox_schoolerp_dashboard_report_alldashboard_managementdashboard_managementdashboardrepository_java")
public class ManagementDashboardRepository extends PageArtifactRepository {
    public ManagementDashboardRepository() {
        super("Dashboard", "report", "ManagementDashboard", "Dashboard/AllDashboard/ManagementDashboard.jsx");
    }
}