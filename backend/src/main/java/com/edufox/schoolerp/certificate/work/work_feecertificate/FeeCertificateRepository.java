package com.edufox.schoolerp.certificate.work.work_feecertificate;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Certificates/Work/FeeCertificate.jsx. */
@Repository("page_com_edufox_schoolerp_certificate_work_work_feecertificate_feecertificaterepository_java")
public class FeeCertificateRepository extends PageArtifactRepository {
    public FeeCertificateRepository() {
        super("Certificates", "work", "FeeCertificate", "Certificates/Work/FeeCertificate.jsx");
    }
}