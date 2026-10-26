package com.edufox.schoolerp.dashboard.report.alldashboard_admindashboard;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Dashboard/AllDashboard/AdminDashboard.jsx. */
@RestController("page_dashboard_report_alldashboard_admindashboard_admindashboardcontroller_java")
@RequestMapping("/api/page-artifacts/dashboard/report/alldashboard_admindashboard")
public class AdminDashboardController extends PageArtifactController<AdminDashboardService> {
    public AdminDashboardController(AdminDashboardService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}