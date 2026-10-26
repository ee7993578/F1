package com.edufox.schoolerp.studentportal.report.report_dashboard;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for StundentPages/Report/Dashboard.jsx. */
@RestController("page_studentportal_report_report_dashboard_dashboardcontroller_java")
@RequestMapping("/api/page-artifacts/stundentpages/report/report_dashboard")
public class DashboardController extends PageArtifactController<DashboardService> {
    public DashboardController(DashboardService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}