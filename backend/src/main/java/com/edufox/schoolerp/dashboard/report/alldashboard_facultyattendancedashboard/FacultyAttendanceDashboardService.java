package com.edufox.schoolerp.dashboard.report.alldashboard_facultyattendancedashboard;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Dashboard/AllDashboard/FacultyAttendanceDashboard.jsx. */
@Service("page_dashboard_report_alldashboard_facultyattendancedashboard_facultyattendancedashboardservice_java")
public class FacultyAttendanceDashboardService extends PageArtifactService<FacultyAttendanceDashboardRepository> {
    public FacultyAttendanceDashboardService(FacultyAttendanceDashboardRepository repository) {
        super(repository);
    }
}