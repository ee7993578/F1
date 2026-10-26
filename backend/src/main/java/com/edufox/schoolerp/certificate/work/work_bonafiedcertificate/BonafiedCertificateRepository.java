package com.edufox.schoolerp.certificate.work.work_bonafiedcertificate;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Certificates/Work/BonafiedCertificate.jsx. */
@Repository("page_com_edufox_schoolerp_certificate_work_work_bonafiedcertificate_bonafiedcertificaterepository_java")
public class BonafiedCertificateRepository extends PageArtifactRepository {
    public BonafiedCertificateRepository() {
        super("Certificates", "work", "BonafiedCertificate", "Certificates/Work/BonafiedCertificate.jsx");
    }
}