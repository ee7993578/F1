package com.edufox.schoolerp.attendance.report.report_totalattendancereport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Attendance/Report/TotalAttendanceReport.jsx. */
@Repository("page_com_edufox_schoolerp_attendance_report_report_totalattendancereport_totalattendancereportrepository_java")
public class TotalAttendanceReportRepository extends PageArtifactRepository {
    public TotalAttendanceReportRepository() {
        super("Attendance", "report", "TotalAttendanceReport", "Attendance/Report/TotalAttendanceReport.jsx");
    }
}