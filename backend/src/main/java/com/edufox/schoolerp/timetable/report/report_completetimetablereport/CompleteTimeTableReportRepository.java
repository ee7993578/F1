package com.edufox.schoolerp.timetable.report.report_completetimetablereport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for TimeTable/Report/CompleteTimeTableReport.jsx. */
@Repository("page_com_edufox_schoolerp_timetable_report_report_completetimetablereport_completetimetablereportrepository_java")
public class CompleteTimeTableReportRepository extends PageArtifactRepository {
    public CompleteTimeTableReportRepository() {
        super("TimeTable", "report", "CompleteTimeTableReport", "TimeTable/Report/CompleteTimeTableReport.jsx");
    }
}