package com.edufox.schoolerp.certificate.work.work_transfercertificate;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Certificates/Work/TransferCertificate.jsx. */
@RestController("page_certificate_work_work_transfercertificate_transfercertificatecontroller_java")
@RequestMapping("/api/page-artifacts/certificates/work/work_transfercertificate")
public class TransferCertificateController extends PageArtifactController<TransferCertificateService> {
    public TransferCertificateController(TransferCertificateService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}