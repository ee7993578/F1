package com.edufox.schoolerp.communication.report.report_smsreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Communication/Report/SmsReport.jsx. */
@Service("page_communication_report_report_smsreport_smsreportservice_java")
public class SmsReportService extends PageArtifactService<SmsReportRepository> {
    public SmsReportService(SmsReportRepository repository) {
        super(repository);
    }
}