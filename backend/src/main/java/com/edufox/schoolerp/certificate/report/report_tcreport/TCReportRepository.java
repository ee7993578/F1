package com.edufox.schoolerp.certificate.report.report_tcreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Certificates/Report/TCReport.jsx. */
@Repository("page_com_edufox_schoolerp_certificate_report_report_tcreport_tcreportrepository_java")
public class TCReportRepository extends PageArtifactRepository {
    public TCReportRepository() {
        super("Certificates", "report", "TCReport", "Certificates/Report/TCReport.jsx");
    }
}