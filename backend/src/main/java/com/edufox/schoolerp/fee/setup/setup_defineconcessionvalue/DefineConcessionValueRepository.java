package com.edufox.schoolerp.fee.setup.setup_defineconcessionvalue;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Fee/Setup/DefineConcessionValue.jsx. */
@Repository("page_com_edufox_schoolerp_fee_setup_setup_defineconcessionvalue_defineconcessionvaluerepository_java")
public class DefineConcessionValueRepository extends PageArtifactRepository {
    public DefineConcessionValueRepository() {
        super("Fee", "setup", "DefineConcessionValue", "Fee/Setup/DefineConcessionValue.jsx");
    }
}