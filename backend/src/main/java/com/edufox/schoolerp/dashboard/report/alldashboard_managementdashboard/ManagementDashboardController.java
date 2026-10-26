package com.edufox.schoolerp.dashboard.report.alldashboard_managementdashboard;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Dashboard/AllDashboard/ManagementDashboard.jsx. */
@RestController("page_dashboard_report_alldashboard_managementdashboard_managementdashboardcontroller_java")
@RequestMapping("/api/page-artifacts/dashboard/report/alldashboard_managementdashboard")
public class ManagementDashboardController extends PageArtifactController<ManagementDashboardService> {
    public ManagementDashboardController(ManagementDashboardService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}