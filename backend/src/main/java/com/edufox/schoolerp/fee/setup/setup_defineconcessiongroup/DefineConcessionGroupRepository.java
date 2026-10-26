package com.edufox.schoolerp.fee.setup.setup_defineconcessiongroup;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Fee/Setup/DefineConcessionGroup.jsx. */
@Repository("page_com_edufox_schoolerp_fee_setup_setup_defineconcessiongroup_defineconcessiongrouprepository_java")
public class DefineConcessionGroupRepository extends PageArtifactRepository {
    public DefineConcessionGroupRepository() {
        super("Fee", "setup", "DefineConcessionGroup", "Fee/Setup/DefineConcessionGroup.jsx");
    }
}