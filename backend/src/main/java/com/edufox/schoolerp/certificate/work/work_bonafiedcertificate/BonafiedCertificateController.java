package com.edufox.schoolerp.certificate.work.work_bonafiedcertificate;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Certificates/Work/BonafiedCertificate.jsx. */
@RestController("page_certificate_work_work_bonafiedcertificate_bonafiedcertificatecontroller_java")
@RequestMapping("/api/page-artifacts/certificates/work/work_bonafiedcertificate")
public class BonafiedCertificateController extends PageArtifactController<BonafiedCertificateService> {
    public BonafiedCertificateController(BonafiedCertificateService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}