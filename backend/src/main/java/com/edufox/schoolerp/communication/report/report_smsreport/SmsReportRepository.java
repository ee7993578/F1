package com.edufox.schoolerp.communication.report.report_smsreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Communication/Report/SmsReport.jsx. */
@Repository("page_com_edufox_schoolerp_communication_report_report_smsreport_smsreportrepository_java")
public class SmsReportRepository extends PageArtifactRepository {
    public SmsReportRepository() {
        super("Communication", "report", "SmsReport", "Communication/Report/SmsReport.jsx");
    }
}