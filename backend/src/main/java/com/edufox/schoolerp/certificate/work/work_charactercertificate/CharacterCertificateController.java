package com.edufox.schoolerp.certificate.work.work_charactercertificate;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Certificates/Work/CharacterCertificate.jsx. */
@RestController("page_certificate_work_work_charactercertificate_charactercertificatecontroller_java")
@RequestMapping("/api/page-artifacts/certificates/work/work_charactercertificate")
public class CharacterCertificateController extends PageArtifactController<CharacterCertificateService> {
    public CharacterCertificateController(CharacterCertificateService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}