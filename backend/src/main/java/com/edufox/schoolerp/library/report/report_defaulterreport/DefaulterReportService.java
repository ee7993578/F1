package com.edufox.schoolerp.library.report.report_defaulterreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Library/Report/DefaulterReport.jsx. */
@Service("page_library_report_report_defaulterreport_defaulterreportservice_java")
public class DefaulterReportService extends PageArtifactService<DefaulterReportRepository> {
    public DefaulterReportService(DefaulterReportRepository repository) {
        super(repository);
    }
}