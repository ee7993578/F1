package com.edufox.schoolerp.certificate.work.work_charactercertificate;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Certificates/Work/CharacterCertificate.jsx. */
@Service("page_certificate_work_work_charactercertificate_charactercertificateservice_java")
public class CharacterCertificateService extends PageArtifactService<CharacterCertificateRepository> {
    public CharacterCertificateService(CharacterCertificateRepository repository) {
        super(repository);
    }
}