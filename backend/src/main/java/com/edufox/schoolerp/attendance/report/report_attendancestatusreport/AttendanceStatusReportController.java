package com.edufox.schoolerp.attendance.report.report_attendancestatusreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Attendance/Report/AttendanceStatusReport.jsx. */
@RestController("page_attendance_report_report_attendancestatusreport_attendancestatusreportcontroller_java")
@RequestMapping("/api/page-artifacts/attendance/report/report_attendancestatusreport")
public class AttendanceStatusReportController extends PageArtifactController<AttendanceStatusReportService> {
    public AttendanceStatusReportController(AttendanceStatusReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}