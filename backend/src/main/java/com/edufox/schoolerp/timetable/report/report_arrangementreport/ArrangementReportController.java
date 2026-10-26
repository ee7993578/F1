package com.edufox.schoolerp.timetable.report.report_arrangementreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for TimeTable/Report/ArrangementReport.jsx. */
@RestController("page_timetable_report_report_arrangementreport_arrangementreportcontroller_java")
@RequestMapping("/api/page-artifacts/timetable/report/report_arrangementreport")
public class ArrangementReportController extends PageArtifactController<ArrangementReportService> {
    public ArrangementReportController(ArrangementReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}