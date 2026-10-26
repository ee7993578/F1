package com.edufox.schoolerp.certificate.work.work_transfercertificate;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Certificates/Work/TransferCertificate.jsx. */
@Service("page_certificate_work_work_transfercertificate_transfercertificateservice_java")
public class TransferCertificateService extends PageArtifactService<TransferCertificateRepository> {
    public TransferCertificateService(TransferCertificateRepository repository) {
        super(repository);
    }
}