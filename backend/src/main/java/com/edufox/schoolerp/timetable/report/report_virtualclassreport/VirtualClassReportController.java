package com.edufox.schoolerp.timetable.report.report_virtualclassreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for TimeTable/Report/VirtualClassReport.jsx. */
@RestController("page_timetable_report_report_virtualclassreport_virtualclassreportcontroller_java")
@RequestMapping("/api/page-artifacts/timetable/report/report_virtualclassreport")
public class VirtualClassReportController extends PageArtifactController<VirtualClassReportService> {
    public VirtualClassReportController(VirtualClassReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}