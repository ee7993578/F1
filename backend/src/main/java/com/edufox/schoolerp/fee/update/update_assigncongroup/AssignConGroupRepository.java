package com.edufox.schoolerp.fee.update.update_assigncongroup;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Fee/Update/AssignConGroup.jsx. */
@Repository("page_com_edufox_schoolerp_fee_update_update_assigncongroup_assigncongrouprepository_java")
public class AssignConGroupRepository extends PageArtifactRepository {
    public AssignConGroupRepository() {
        super("Fee", "update", "AssignConGroup", "Fee/Update/AssignConGroup.jsx");
    }
}