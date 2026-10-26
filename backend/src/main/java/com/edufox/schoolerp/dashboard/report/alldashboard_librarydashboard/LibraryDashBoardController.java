package com.edufox.schoolerp.dashboard.report.alldashboard_librarydashboard;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Dashboard/AllDashboard/LibraryDashBoard.jsx. */
@RestController("page_dashboard_report_alldashboard_librarydashboard_librarydashboardcontroller_java")
@RequestMapping("/api/page-artifacts/dashboard/report/alldashboard_librarydashboard")
public class LibraryDashBoardController extends PageArtifactController<LibraryDashBoardService> {
    public LibraryDashBoardController(LibraryDashBoardService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}