package com.edufox.schoolerp.certificate.work.work_feecertificate;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Certificates/Work/FeeCertificate.jsx. */
@RestController("page_certificate_work_work_feecertificate_feecertificatecontroller_java")
@RequestMapping("/api/page-artifacts/certificates/work/work_feecertificate")
public class FeeCertificateController extends PageArtifactController<FeeCertificateService> {
    public FeeCertificateController(FeeCertificateService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}