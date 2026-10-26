package com.edufox.schoolerp.library.report.report_bookreturnreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Library/Report/BookReturnReport.jsx. */
@Repository("page_com_edufox_schoolerp_library_report_report_bookreturnreport_bookreturnreportrepository_java")
public class BookReturnReportRepository extends PageArtifactRepository {
    public BookReturnReportRepository() {
        super("Library", "report", "BookReturnReport", "Library/Report/BookReturnReport.jsx");
    }
}