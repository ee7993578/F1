package com.edufox.schoolerp.dashboard.report.alldashboard_librarydashboard;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Dashboard/AllDashboard/LibraryDashBoard.jsx. */
@Repository("page_com_edufox_schoolerp_dashboard_report_alldashboard_librarydashboard_librarydashboardrepository_java")
public class LibraryDashBoardRepository extends PageArtifactRepository {
    public LibraryDashBoardRepository() {
        super("Dashboard", "report", "LibraryDashBoard", "Dashboard/AllDashboard/LibraryDashBoard.jsx");
    }
}