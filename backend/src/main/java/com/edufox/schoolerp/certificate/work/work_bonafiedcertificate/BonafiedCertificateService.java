package com.edufox.schoolerp.certificate.work.work_bonafiedcertificate;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Certificates/Work/BonafiedCertificate.jsx. */
@Service("page_certificate_work_work_bonafiedcertificate_bonafiedcertificateservice_java")
public class BonafiedCertificateService extends PageArtifactService<BonafiedCertificateRepository> {
    public BonafiedCertificateService(BonafiedCertificateRepository repository) {
        super(repository);
    }
}