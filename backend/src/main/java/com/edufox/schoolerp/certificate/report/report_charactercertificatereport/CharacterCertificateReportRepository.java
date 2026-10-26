package com.edufox.schoolerp.certificate.report.report_charactercertificatereport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Certificates/Report/CharacterCertificateReport.jsx. */
@Repository("page_com_edufox_schoolerp_certificate_report_report_charactercertificatereport_charactercertificatereportrepository_java")
public class CharacterCertificateReportRepository extends PageArtifactRepository {
    public CharacterCertificateReportRepository() {
        super("Certificates", "report", "CharacterCertificateReport", "Certificates/Report/CharacterCertificateReport.jsx");
    }
}