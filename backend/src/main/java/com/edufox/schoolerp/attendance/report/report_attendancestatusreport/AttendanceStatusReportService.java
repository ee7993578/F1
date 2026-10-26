package com.edufox.schoolerp.attendance.report.report_attendancestatusreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Attendance/Report/AttendanceStatusReport.jsx. */
@Service("page_attendance_report_report_attendancestatusreport_attendancestatusreportservice_java")
public class AttendanceStatusReportService extends PageArtifactService<AttendanceStatusReportRepository> {
    public AttendanceStatusReportService(AttendanceStatusReportRepository repository) {
        super(repository);
    }
}