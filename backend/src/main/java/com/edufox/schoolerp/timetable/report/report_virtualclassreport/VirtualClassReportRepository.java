package com.edufox.schoolerp.timetable.report.report_virtualclassreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for TimeTable/Report/VirtualClassReport.jsx. */
@Repository("page_com_edufox_schoolerp_timetable_report_report_virtualclassreport_virtualclassreportrepository_java")
public class VirtualClassReportRepository extends PageArtifactRepository {
    public VirtualClassReportRepository() {
        super("TimeTable", "report", "VirtualClassReport", "TimeTable/Report/VirtualClassReport.jsx");
    }
}