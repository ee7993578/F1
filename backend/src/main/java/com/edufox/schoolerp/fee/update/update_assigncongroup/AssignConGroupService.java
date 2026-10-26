package com.edufox.schoolerp.fee.update.update_assigncongroup;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Fee/Update/AssignConGroup.jsx. */
@Service("page_fee_update_update_assigncongroup_assigncongroupservice_java")
public class AssignConGroupService extends PageArtifactService<AssignConGroupRepository> {
    public AssignConGroupService(AssignConGroupRepository repository) {
        super(repository);
    }
}