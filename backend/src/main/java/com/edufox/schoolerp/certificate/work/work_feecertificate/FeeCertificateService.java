package com.edufox.schoolerp.certificate.work.work_feecertificate;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Certificates/Work/FeeCertificate.jsx. */
@Service("page_certificate_work_work_feecertificate_feecertificateservice_java")
public class FeeCertificateService extends PageArtifactService<FeeCertificateRepository> {
    public FeeCertificateService(FeeCertificateRepository repository) {
        super(repository);
    }
}