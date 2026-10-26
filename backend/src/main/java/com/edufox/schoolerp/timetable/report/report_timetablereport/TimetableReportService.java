package com.edufox.schoolerp.timetable.report.report_timetablereport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for TimeTable/Report/TimetableReport.jsx. */
@Service("page_timetable_report_report_timetablereport_timetablereportservice_java")
public class TimetableReportService extends PageArtifactService<TimetableReportRepository> {
    public TimetableReportService(TimetableReportRepository repository) {
        super(repository);
    }
}