package com.edufox.schoolerp.dashboard.report.alldashboard_librarydashboard;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Dashboard/AllDashboard/LibraryDashBoard.jsx. */
@Service("page_dashboard_report_alldashboard_librarydashboard_librarydashboardservice_java")
public class LibraryDashBoardService extends PageArtifactService<LibraryDashBoardRepository> {
    public LibraryDashBoardService(LibraryDashBoardRepository repository) {
        super(repository);
    }
}