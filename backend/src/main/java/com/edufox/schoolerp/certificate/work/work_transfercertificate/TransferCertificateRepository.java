package com.edufox.schoolerp.certificate.work.work_transfercertificate;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Certificates/Work/TransferCertificate.jsx. */
@Repository("page_com_edufox_schoolerp_certificate_work_work_transfercertificate_transfercertificaterepository_java")
public class TransferCertificateRepository extends PageArtifactRepository {
    public TransferCertificateRepository() {
        super("Certificates", "work", "TransferCertificate", "Certificates/Work/TransferCertificate.jsx");
    }
}