package com.edufox.schoolerp.dashboard.report.alldashboard_admindashboardmarks;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Dashboard/AllDashboard/AdminDashBoardMarks.jsx. */
@RestController("page_dashboard_report_alldashboard_admindashboardmarks_admindashboardmarkscontroller_java")
@RequestMapping("/api/page-artifacts/dashboard/report/alldashboard_admindashboardmarks")
public class AdminDashBoardMarksController extends PageArtifactController<AdminDashBoardMarksService> {
    public AdminDashBoardMarksController(AdminDashBoardMarksService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}