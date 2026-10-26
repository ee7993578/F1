package com.edufox.schoolerp.library.report.report_bookreturnreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Library/Report/BookReturnReport.jsx. */
@RestController("page_library_report_report_bookreturnreport_bookreturnreportcontroller_java")
@RequestMapping("/api/page-artifacts/library/report/report_bookreturnreport")
public class BookReturnReportController extends PageArtifactController<BookReturnReportService> {
    public BookReturnReportController(BookReturnReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}