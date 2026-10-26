package com.edufox.schoolerp.fee.setup.setup_defineconcessiongroup;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Fee/Setup/DefineConcessionGroup.jsx. */
@Service("page_fee_setup_setup_defineconcessiongroup_defineconcessiongroupservice_java")
public class DefineConcessionGroupService extends PageArtifactService<DefineConcessionGroupRepository> {
    public DefineConcessionGroupService(DefineConcessionGroupRepository repository) {
        super(repository);
    }
}