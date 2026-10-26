package com.edufox.schoolerp.certificate.report.report_bonafidecertificatereport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Certificates/Report/BonafideCertificateReport.jsx. */
@Repository("page_com_edufox_schoolerp_certificate_report_report_bonafidecertificatereport_bonafidecertificatereportrepository_java")
public class BonafideCertificateReportRepository extends PageArtifactRepository {
    public BonafideCertificateReportRepository() {
        super("Certificates", "report", "BonafideCertificateReport", "Certificates/Report/BonafideCertificateReport.jsx");
    }
}