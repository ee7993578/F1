package com.edufox.schoolerp.fee.setup.setup_defineconcession;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Fee/Setup/DefineConcession.jsx. */
@Repository("page_com_edufox_schoolerp_fee_setup_setup_defineconcession_defineconcessionrepository_java")
public class DefineConcessionRepository extends PageArtifactRepository {
    public DefineConcessionRepository() {
        super("Fee", "setup", "DefineConcession", "Fee/Setup/DefineConcession.jsx");
    }
}