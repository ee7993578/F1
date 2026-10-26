package com.edufox.schoolerp.attendance.report.report_totalattendancereport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Attendance/Report/TotalAttendanceReport.jsx. */
@RestController("page_attendance_report_report_totalattendancereport_totalattendancereportcontroller_java")
@RequestMapping("/api/page-artifacts/attendance/report/report_totalattendancereport")
public class TotalAttendanceReportController extends PageArtifactController<TotalAttendanceReportService> {
    public TotalAttendanceReportController(TotalAttendanceReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}