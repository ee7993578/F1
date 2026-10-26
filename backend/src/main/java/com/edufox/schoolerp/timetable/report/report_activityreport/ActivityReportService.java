package com.edufox.schoolerp.timetable.report.report_activityreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for TimeTable/Report/ActivityReport.jsx. */
@Service("page_timetable_report_report_activityreport_activityreportservice_java")
public class ActivityReportService extends PageArtifactService<ActivityReportRepository> {
    public ActivityReportService(ActivityReportRepository repository) {
        super(repository);
    }
}