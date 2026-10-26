package com.edufox.schoolerp.communication.report.report_emailreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Communication/Report/EmailReport.jsx. */
@Repository("page_com_edufox_schoolerp_communication_report_report_emailreport_emailreportrepository_java")
public class EmailReportRepository extends PageArtifactRepository {
    public EmailReportRepository() {
        super("Communication", "report", "EmailReport", "Communication/Report/EmailReport.jsx");
    }
}