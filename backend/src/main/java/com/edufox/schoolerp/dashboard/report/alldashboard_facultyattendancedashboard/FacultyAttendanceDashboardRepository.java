package com.edufox.schoolerp.dashboard.report.alldashboard_facultyattendancedashboard;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Dashboard/AllDashboard/FacultyAttendanceDashboard.jsx. */
@Repository("page_com_edufox_schoolerp_dashboard_report_alldashboard_facultyattendancedashboard_facultyattendancedashboardrepository_java")
public class FacultyAttendanceDashboardRepository extends PageArtifactRepository {
    public FacultyAttendanceDashboardRepository() {
        super("Dashboard", "report", "FacultyAttendanceDashboard", "Dashboard/AllDashboard/FacultyAttendanceDashboard.jsx");
    }
}