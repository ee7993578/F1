package com.edufox.schoolerp.dashboard.report.alldashboard_admindashboard;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Dashboard/AllDashboard/AdminDashboard.jsx. */
@Service("page_dashboard_report_alldashboard_admindashboard_admindashboardservice_java")
public class AdminDashboardService extends PageArtifactService<AdminDashboardRepository> {
    public AdminDashboardService(AdminDashboardRepository repository) {
        super(repository);
    }
}