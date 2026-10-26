package com.edufox.schoolerp.certificate.report.report_tcreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Certificates/Report/TCReport.jsx. */
@Service("page_certificate_report_report_tcreport_tcreportservice_java")
public class TCReportService extends PageArtifactService<TCReportRepository> {
    public TCReportService(TCReportRepository repository) {
        super(repository);
    }
}