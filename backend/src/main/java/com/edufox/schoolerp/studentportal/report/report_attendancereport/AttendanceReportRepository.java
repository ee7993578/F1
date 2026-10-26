package com.edufox.schoolerp.studentportal.report.report_attendancereport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for StundentPages/Report/AttendanceReport.jsx. */
@Repository("page_com_edufox_schoolerp_studentportal_report_report_attendancereport_attendancereportrepository_java")
public class AttendanceReportRepository extends PageArtifactRepository {
    public AttendanceReportRepository() {
        super("StundentPages", "report", "AttendanceReport", "StundentPages/Report/AttendanceReport.jsx");
    }
}