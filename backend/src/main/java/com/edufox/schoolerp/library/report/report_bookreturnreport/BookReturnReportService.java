package com.edufox.schoolerp.library.report.report_bookreturnreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Library/Report/BookReturnReport.jsx. */
@Service("page_library_report_report_bookreturnreport_bookreturnreportservice_java")
public class BookReturnReportService extends PageArtifactService<BookReturnReportRepository> {
    public BookReturnReportService(BookReturnReportRepository repository) {
        super(repository);
    }
}