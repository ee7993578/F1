package com.edufox.schoolerp.fee.setup.setup_defineconcessionvalue;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Fee/Setup/DefineConcessionValue.jsx. */
@Service("page_fee_setup_setup_defineconcessionvalue_defineconcessionvalueservice_java")
public class DefineConcessionValueService extends PageArtifactService<DefineConcessionValueRepository> {
    public DefineConcessionValueService(DefineConcessionValueRepository repository) {
        super(repository);
    }
}