package com.edufox.schoolerp.attendance.report.report_attendancestatusreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Attendance/Report/AttendanceStatusReport.jsx. */
@Repository("page_com_edufox_schoolerp_attendance_report_report_attendancestatusreport_attendancestatusreportrepository_java")
public class AttendanceStatusReportRepository extends PageArtifactRepository {
    public AttendanceStatusReportRepository() {
        super("Attendance", "report", "AttendanceStatusReport", "Attendance/Report/AttendanceStatusReport.jsx");
    }
}