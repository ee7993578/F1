package com.edufox.schoolerp.timetable.report.report_completetimetablereport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for TimeTable/Report/CompleteTimeTableReport.jsx. */
@Service("page_timetable_report_report_completetimetablereport_completetimetablereportservice_java")
public class CompleteTimeTableReportService extends PageArtifactService<CompleteTimeTableReportRepository> {
    public CompleteTimeTableReportService(CompleteTimeTableReportRepository repository) {
        super(repository);
    }
}