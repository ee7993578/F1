package com.edufox.schoolerp.certificate.report.report_bonafidecertificatereport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Certificates/Report/BonafideCertificateReport.jsx. */
@RestController("page_certificate_report_report_bonafidecertificatereport_bonafidecertificatereportcontroller_java")
@RequestMapping("/api/page-artifacts/certificates/report/report_bonafidecertificatereport")
public class BonafideCertificateReportController extends PageArtifactController<BonafideCertificateReportService> {
    public BonafideCertificateReportController(BonafideCertificateReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}