package com.edufox.schoolerp.timetable.report.report_completetimetablereport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for TimeTable/Report/CompleteTimeTableReport.jsx. */
@RestController("page_timetable_report_report_completetimetablereport_completetimetablereportcontroller_java")
@RequestMapping("/api/page-artifacts/timetable/report/report_completetimetablereport")
public class CompleteTimeTableReportController extends PageArtifactController<CompleteTimeTableReportService> {
    public CompleteTimeTableReportController(CompleteTimeTableReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}