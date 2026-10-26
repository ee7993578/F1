package com.edufox.schoolerp.certificate.work.work_incomecertificate;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Certificates/Work/IncomeCertificate.jsx. */
@RestController("page_certificate_work_work_incomecertificate_incomecertificatecontroller_java")
@RequestMapping("/api/page-artifacts/certificates/work/work_incomecertificate")
public class IncomeCertificateController extends PageArtifactController<IncomeCertificateService> {
    public IncomeCertificateController(IncomeCertificateService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}