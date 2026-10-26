package com.edufox.schoolerp.studentportal.report.report_dashboard;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for StundentPages/Report/Dashboard.jsx. */
@Service("page_studentportal_report_report_dashboard_dashboardservice_java")
public class DashboardService extends PageArtifactService<DashboardRepository> {
    public DashboardService(DashboardRepository repository) {
        super(repository);
    }
}