package com.edufox.schoolerp.timetable.report.report_activityreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for TimeTable/Report/ActivityReport.jsx. */
@Repository("page_com_edufox_schoolerp_timetable_report_report_activityreport_activityreportrepository_java")
public class ActivityReportRepository extends PageArtifactRepository {
    public ActivityReportRepository() {
        super("TimeTable", "report", "ActivityReport", "TimeTable/Report/ActivityReport.jsx");
    }
}