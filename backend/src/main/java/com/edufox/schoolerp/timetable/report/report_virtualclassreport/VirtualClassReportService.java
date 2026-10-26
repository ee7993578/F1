package com.edufox.schoolerp.timetable.report.report_virtualclassreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for TimeTable/Report/VirtualClassReport.jsx. */
@Service("page_timetable_report_report_virtualclassreport_virtualclassreportservice_java")
public class VirtualClassReportService extends PageArtifactService<VirtualClassReportRepository> {
    public VirtualClassReportService(VirtualClassReportRepository repository) {
        super(repository);
    }
}