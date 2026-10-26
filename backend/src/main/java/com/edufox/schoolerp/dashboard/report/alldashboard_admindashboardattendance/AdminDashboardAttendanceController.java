package com.edufox.schoolerp.dashboard.report.alldashboard_admindashboardattendance;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Dashboard/AllDashboard/AdminDashboardAttendance.jsx. */
@RestController("page_dashboard_report_alldashboard_admindashboardattendance_admindashboardattendancecontroller_java")
@RequestMapping("/api/page-artifacts/dashboard/report/alldashboard_admindashboardattendance")
public class AdminDashboardAttendanceController extends PageArtifactController<AdminDashboardAttendanceService> {
    public AdminDashboardAttendanceController(AdminDashboardAttendanceService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}