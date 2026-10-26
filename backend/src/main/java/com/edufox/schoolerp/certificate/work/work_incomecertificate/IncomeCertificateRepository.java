package com.edufox.schoolerp.certificate.work.work_incomecertificate;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Certificates/Work/IncomeCertificate.jsx. */
@Repository("page_com_edufox_schoolerp_certificate_work_work_incomecertificate_incomecertificaterepository_java")
public class IncomeCertificateRepository extends PageArtifactRepository {
    public IncomeCertificateRepository() {
        super("Certificates", "work", "IncomeCertificate", "Certificates/Work/IncomeCertificate.jsx");
    }
}