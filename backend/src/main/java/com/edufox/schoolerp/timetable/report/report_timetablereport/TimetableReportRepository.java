package com.edufox.schoolerp.timetable.report.report_timetablereport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for TimeTable/Report/TimetableReport.jsx. */
@Repository("page_com_edufox_schoolerp_timetable_report_report_timetablereport_timetablereportrepository_java")
public class TimetableReportRepository extends PageArtifactRepository {
    public TimetableReportRepository() {
        super("TimeTable", "report", "TimetableReport", "TimeTable/Report/TimetableReport.jsx");
    }
}