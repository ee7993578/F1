package com.edufox.schoolerp.certificate.report.report_bonafidecertificatereport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Certificates/Report/BonafideCertificateReport.jsx. */
@Service("page_certificate_report_report_bonafidecertificatereport_bonafidecertificatereportservice_java")
public class BonafideCertificateReportService extends PageArtifactService<BonafideCertificateReportRepository> {
    public BonafideCertificateReportService(BonafideCertificateReportRepository repository) {
        super(repository);
    }
}