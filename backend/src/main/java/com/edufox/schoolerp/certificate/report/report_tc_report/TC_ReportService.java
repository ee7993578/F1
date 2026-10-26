package com.edufox.schoolerp.certificate.report.report_tc_report;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Certificates/Report/TC_Report.jsx. */
@Service("page_certificate_report_report_tc_report_tc_reportservice_java")
public class TC_ReportService extends PageArtifactService<TC_ReportRepository> {
    public TC_ReportService(TC_ReportRepository repository) {
        super(repository);
    }
}