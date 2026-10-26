package com.edufox.schoolerp.certificate.report.report_charactercertificatereport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Certificates/Report/CharacterCertificateReport.jsx. */
@Service("page_certificate_report_report_charactercertificatereport_charactercertificatereportservice_java")
public class CharacterCertificateReportService extends PageArtifactService<CharacterCertificateReportRepository> {
    public CharacterCertificateReportService(CharacterCertificateReportRepository repository) {
        super(repository);
    }
}