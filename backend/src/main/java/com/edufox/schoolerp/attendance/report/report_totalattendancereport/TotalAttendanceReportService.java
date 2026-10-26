package com.edufox.schoolerp.attendance.report.report_totalattendancereport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Attendance/Report/TotalAttendanceReport.jsx. */
@Service("page_attendance_report_report_totalattendancereport_totalattendancereportservice_java")
public class TotalAttendanceReportService extends PageArtifactService<TotalAttendanceReportRepository> {
    public TotalAttendanceReportService(TotalAttendanceReportRepository repository) {
        super(repository);
    }
}