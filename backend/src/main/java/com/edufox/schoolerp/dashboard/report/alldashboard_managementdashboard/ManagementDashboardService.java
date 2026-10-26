package com.edufox.schoolerp.dashboard.report.alldashboard_managementdashboard;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Dashboard/AllDashboard/ManagementDashboard.jsx. */
@Service("page_dashboard_report_alldashboard_managementdashboard_managementdashboardservice_java")
public class ManagementDashboardService extends PageArtifactService<ManagementDashboardRepository> {
    public ManagementDashboardService(ManagementDashboardRepository repository) {
        super(repository);
    }
}