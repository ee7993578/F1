package com.edufox.schoolerp.fee.setup.setup_defineconcession;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Fee/Setup/DefineConcession.jsx. */
@Service("page_fee_setup_setup_defineconcession_defineconcessionservice_java")
public class DefineConcessionService extends PageArtifactService<DefineConcessionRepository> {
    public DefineConcessionService(DefineConcessionRepository repository) {
        super(repository);
    }
}