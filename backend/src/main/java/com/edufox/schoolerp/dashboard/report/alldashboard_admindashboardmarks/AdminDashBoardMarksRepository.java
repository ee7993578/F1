package com.edufox.schoolerp.dashboard.report.alldashboard_admindashboardmarks;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Dashboard/AllDashboard/AdminDashBoardMarks.jsx. */
@Repository("page_com_edufox_schoolerp_dashboard_report_alldashboard_admindashboardmarks_admindashboardmarksrepository_java")
public class AdminDashBoardMarksRepository extends PageArtifactRepository {
    public AdminDashBoardMarksRepository() {
        super("Dashboard", "report", "AdminDashBoardMarks", "Dashboard/AllDashboard/AdminDashBoardMarks.jsx");
    }
}