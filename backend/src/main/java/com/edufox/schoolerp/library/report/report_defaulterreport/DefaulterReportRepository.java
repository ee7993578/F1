package com.edufox.schoolerp.library.report.report_defaulterreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Library/Report/DefaulterReport.jsx. */
@Repository("page_com_edufox_schoolerp_library_report_report_defaulterreport_defaulterreportrepository_java")
public class DefaulterReportRepository extends PageArtifactRepository {
    public DefaulterReportRepository() {
        super("Library", "report", "DefaulterReport", "Library/Report/DefaulterReport.jsx");
    }
}