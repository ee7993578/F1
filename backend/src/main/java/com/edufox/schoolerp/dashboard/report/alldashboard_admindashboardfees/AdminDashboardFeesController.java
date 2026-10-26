package com.edufox.schoolerp.dashboard.report.alldashboard_admindashboardfees;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Dashboard/AllDashboard/AdminDashboardFees.jsx. */
@RestController("page_dashboard_report_alldashboard_admindashboardfees_admindashboardfeescontroller_java")
@RequestMapping("/api/page-artifacts/dashboard/report/alldashboard_admindashboardfees")
public class AdminDashboardFeesController extends PageArtifactController<AdminDashboardFeesService> {
    public AdminDashboardFeesController(AdminDashboardFeesService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}