package com.edufox.schoolerp.certificate.report.report_charactercertificatereport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Certificates/Report/CharacterCertificateReport.jsx. */
@RestController("page_certificate_report_report_charactercertificatereport_charactercertificatereportcontroller_java")
@RequestMapping("/api/page-artifacts/certificates/report/report_charactercertificatereport")
public class CharacterCertificateReportController extends PageArtifactController<CharacterCertificateReportService> {
    public CharacterCertificateReportController(CharacterCertificateReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}