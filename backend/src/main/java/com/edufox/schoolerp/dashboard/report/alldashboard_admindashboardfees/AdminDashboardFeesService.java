package com.edufox.schoolerp.dashboard.report.alldashboard_admindashboardfees;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Dashboard/AllDashboard/AdminDashboardFees.jsx. */
@Service("page_dashboard_report_alldashboard_admindashboardfees_admindashboardfeesservice_java")
public class AdminDashboardFeesService extends PageArtifactService<AdminDashboardFeesRepository> {
    public AdminDashboardFeesService(AdminDashboardFeesRepository repository) {
        super(repository);
    }
}