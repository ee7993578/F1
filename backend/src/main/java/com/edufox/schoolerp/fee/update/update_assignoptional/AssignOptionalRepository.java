package com.edufox.schoolerp.fee.update.update_assignoptional;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Fee/Update/AssignOptional.jsx. */
@Repository("page_com_edufox_schoolerp_fee_update_update_assignoptional_assignoptionalrepository_java")
public class AssignOptionalRepository extends PageArtifactRepository {
    public AssignOptionalRepository() {
        super("Fee", "update", "AssignOptional", "Fee/Update/AssignOptional.jsx");
    }
}