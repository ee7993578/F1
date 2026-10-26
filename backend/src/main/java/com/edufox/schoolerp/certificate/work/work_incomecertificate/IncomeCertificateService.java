package com.edufox.schoolerp.certificate.work.work_incomecertificate;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Certificates/Work/IncomeCertificate.jsx. */
@Service("page_certificate_work_work_incomecertificate_incomecertificateservice_java")
public class IncomeCertificateService extends PageArtifactService<IncomeCertificateRepository> {
    public IncomeCertificateService(IncomeCertificateRepository repository) {
        super(repository);
    }
}