package com.edufox.schoolerp.timetable.report.report_schooltimetablereport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for TimeTable/Report/SchoolTimetableReport.jsx. */
@Repository("page_com_edufox_schoolerp_timetable_report_report_schooltimetablereport_schooltimetablereportrepository_java")
public class SchoolTimetableReportRepository extends PageArtifactRepository {
    public SchoolTimetableReportRepository() {
        super("TimeTable", "report", "SchoolTimetableReport", "TimeTable/Report/SchoolTimetableReport.jsx");
    }
}