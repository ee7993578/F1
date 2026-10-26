package com.edufox.schoolerp.dashboard.report.alldashboard_admindashboardattendance;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Dashboard/AllDashboard/AdminDashboardAttendance.jsx. */
@Repository("page_com_edufox_schoolerp_dashboard_report_alldashboard_admindashboardattendance_admindashboardattendancerepository_java")
public class AdminDashboardAttendanceRepository extends PageArtifactRepository {
    public AdminDashboardAttendanceRepository() {
        super("Dashboard", "report", "AdminDashboardAttendance", "Dashboard/AllDashboard/AdminDashboardAttendance.jsx");
    }
}