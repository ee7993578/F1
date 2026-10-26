package com.edufox.schoolerp.dashboard.report.alldashboard_admindashboardattendance;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Dashboard/AllDashboard/AdminDashboardAttendance.jsx. */
@Service("page_dashboard_report_alldashboard_admindashboardattendance_admindashboardattendanceservice_java")
public class AdminDashboardAttendanceService extends PageArtifactService<AdminDashboardAttendanceRepository> {
    public AdminDashboardAttendanceService(AdminDashboardAttendanceRepository repository) {
        super(repository);
    }
}