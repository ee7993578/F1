package com.edufox.schoolerp.fee.update.update_feeupdatedynamic;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Fee/Update/FeeUpdateDynamic.jsx. */
@Repository("page_com_edufox_schoolerp_fee_update_update_feeupdatedynamic_feeupdatedynamicrepository_java")
public class FeeUpdateDynamicRepository extends PageArtifactRepository {
    public FeeUpdateDynamicRepository() {
        super("Fee", "update", "FeeUpdateDynamic", "Fee/Update/FeeUpdateDynamic.jsx");
    }
}