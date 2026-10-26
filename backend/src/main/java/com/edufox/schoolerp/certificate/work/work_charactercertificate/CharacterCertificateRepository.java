package com.edufox.schoolerp.certificate.work.work_charactercertificate;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Certificates/Work/CharacterCertificate.jsx. */
@Repository("page_com_edufox_schoolerp_certificate_work_work_charactercertificate_charactercertificaterepository_java")
public class CharacterCertificateRepository extends PageArtifactRepository {
    public CharacterCertificateRepository() {
        super("Certificates", "work", "CharacterCertificate", "Certificates/Work/CharacterCertificate.jsx");
    }
}