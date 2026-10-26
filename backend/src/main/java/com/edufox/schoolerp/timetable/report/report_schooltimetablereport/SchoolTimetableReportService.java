package com.edufox.schoolerp.timetable.report.report_schooltimetablereport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for TimeTable/Report/SchoolTimetableReport.jsx. */
@Service("page_timetable_report_report_schooltimetablereport_schooltimetablereportservice_java")
public class SchoolTimetableReportService extends PageArtifactService<SchoolTimetableReportRepository> {
    public SchoolTimetableReportService(SchoolTimetableReportRepository repository) {
        super(repository);
    }
}