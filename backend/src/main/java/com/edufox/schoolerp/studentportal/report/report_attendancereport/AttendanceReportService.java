package com.edufox.schoolerp.studentportal.report.report_attendancereport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for StundentPages/Report/AttendanceReport.jsx. */
@Service("page_studentportal_report_report_attendancereport_attendancereportservice_java")
public class AttendanceReportService extends PageArtifactService<AttendanceReportRepository> {
    public AttendanceReportService(AttendanceReportRepository repository) {
        super(repository);
    }
}