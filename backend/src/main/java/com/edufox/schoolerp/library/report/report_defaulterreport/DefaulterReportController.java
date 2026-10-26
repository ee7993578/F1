package com.edufox.schoolerp.library.report.report_defaulterreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Library/Report/DefaulterReport.jsx. */
@RestController("page_library_report_report_defaulterreport_defaulterreportcontroller_java")
@RequestMapping("/api/page-artifacts/library/report/report_defaulterreport")
public class DefaulterReportController extends PageArtifactController<DefaulterReportService> {
    public DefaulterReportController(DefaulterReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}