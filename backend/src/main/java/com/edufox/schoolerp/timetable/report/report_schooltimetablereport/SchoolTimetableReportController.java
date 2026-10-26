package com.edufox.schoolerp.timetable.report.report_schooltimetablereport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for TimeTable/Report/SchoolTimetableReport.jsx. */
@RestController("page_timetable_report_report_schooltimetablereport_schooltimetablereportcontroller_java")
@RequestMapping("/api/page-artifacts/timetable/report/report_schooltimetablereport")
public class SchoolTimetableReportController extends PageArtifactController<SchoolTimetableReportService> {
    public SchoolTimetableReportController(SchoolTimetableReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}