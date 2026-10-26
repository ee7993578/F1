package com.edufox.schoolerp.uploads.report.report_dailyhomeworkreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Uploads/Report/DailyHomeworkReport.jsx. */
@RestController("page_uploads_report_report_dailyhomeworkreport_dailyhomeworkreportcontroller_java")
@RequestMapping("/api/page-artifacts/uploads/report/report_dailyhomeworkreport")
public class DailyHomeworkReportController extends PageArtifactController<DailyHomeworkReportService> {
    public DailyHomeworkReportController(DailyHomeworkReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}