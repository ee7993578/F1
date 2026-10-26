package com.edufox.schoolerp.attendance.report.report_attendancereportpercentage;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Attendance/Report/AttendanceReportPercentage.jsx. */
@Repository("page_com_edufox_schoolerp_attendance_report_report_attendancereportpercentage_attendancereportpercentagerepository_java")
public class AttendanceReportPercentageRepository extends PageArtifactRepository {
    public AttendanceReportPercentageRepository() {
        super("Attendance", "report", "AttendanceReportPercentage", "Attendance/Report/AttendanceReportPercentage.jsx");
    }
}