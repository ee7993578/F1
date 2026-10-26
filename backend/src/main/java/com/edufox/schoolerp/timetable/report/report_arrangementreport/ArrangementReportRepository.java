package com.edufox.schoolerp.timetable.report.report_arrangementreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for TimeTable/Report/ArrangementReport.jsx. */
@Repository("page_com_edufox_schoolerp_timetable_report_report_arrangementreport_arrangementreportrepository_java")
public class ArrangementReportRepository extends PageArtifactRepository {
    public ArrangementReportRepository() {
        super("TimeTable", "report", "ArrangementReport", "TimeTable/Report/ArrangementReport.jsx");
    }
}