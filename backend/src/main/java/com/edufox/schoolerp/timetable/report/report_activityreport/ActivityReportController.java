package com.edufox.schoolerp.timetable.report.report_activityreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for TimeTable/Report/ActivityReport.jsx. */
@RestController("page_timetable_report_report_activityreport_activityreportcontroller_java")
@RequestMapping("/api/page-artifacts/timetable/report/report_activityreport")
public class ActivityReportController extends PageArtifactController<ActivityReportService> {
    public ActivityReportController(ActivityReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}