package com.edufox.schoolerp.attendance.report.report_attendancereportpercentage;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Attendance/Report/AttendanceReportPercentage.jsx. */
@RestController("page_attendance_report_report_attendancereportpercentage_attendancereportpercentagecontroller_java")
@RequestMapping("/api/page-artifacts/attendance/report/report_attendancereportpercentage")
public class AttendanceReportPercentageController extends PageArtifactController<AttendanceReportPercentageService> {
    public AttendanceReportPercentageController(AttendanceReportPercentageService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}