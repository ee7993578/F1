package com.edufox.schoolerp.communication.report.report_emailreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Communication/Report/EmailReport.jsx. */
@Service("page_communication_report_report_emailreport_emailreportservice_java")
public class EmailReportService extends PageArtifactService<EmailReportRepository> {
    public EmailReportService(EmailReportRepository repository) {
        super(repository);
    }
}