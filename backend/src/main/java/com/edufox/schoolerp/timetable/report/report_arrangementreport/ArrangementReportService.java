package com.edufox.schoolerp.timetable.report.report_arrangementreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for TimeTable/Report/ArrangementReport.jsx. */
@Service("page_timetable_report_report_arrangementreport_arrangementreportservice_java")
public class ArrangementReportService extends PageArtifactService<ArrangementReportRepository> {
    public ArrangementReportService(ArrangementReportRepository repository) {
        super(repository);
    }
}