package com.edufox.schoolerp.dashboard.report.alldashboard_admindashboardmarks;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Dashboard/AllDashboard/AdminDashBoardMarks.jsx. */
@Service("page_dashboard_report_alldashboard_admindashboardmarks_admindashboardmarksservice_java")
public class AdminDashBoardMarksService extends PageArtifactService<AdminDashBoardMarksRepository> {
    public AdminDashBoardMarksService(AdminDashBoardMarksRepository repository) {
        super(repository);
    }
}