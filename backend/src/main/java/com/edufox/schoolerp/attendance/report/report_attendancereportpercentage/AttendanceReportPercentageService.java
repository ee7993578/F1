package com.edufox.schoolerp.attendance.report.report_attendancereportpercentage;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Attendance/Report/AttendanceReportPercentage.jsx. */
@Service("page_attendance_report_report_attendancereportpercentage_attendancereportpercentageservice_java")
public class AttendanceReportPercentageService extends PageArtifactService<AttendanceReportPercentageRepository> {
    public AttendanceReportPercentageService(AttendanceReportPercentageRepository repository) {
        super(repository);
    }
}