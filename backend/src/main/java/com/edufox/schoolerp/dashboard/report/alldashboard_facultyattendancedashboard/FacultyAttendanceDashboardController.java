package com.edufox.schoolerp.dashboard.report.alldashboard_facultyattendancedashboard;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Dashboard/AllDashboard/FacultyAttendanceDashboard.jsx. */
@RestController("page_dashboard_report_alldashboard_facultyattendancedashboard_facultyattendancedashboardcontroller_java")
@RequestMapping("/api/page-artifacts/dashboard/report/alldashboard_facultyattendancedashboard")
public class FacultyAttendanceDashboardController extends PageArtifactController<FacultyAttendanceDashboardService> {
    public FacultyAttendanceDashboardController(FacultyAttendanceDashboardService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}