package com.edufox.schoolerp.fee.update.update_feeupdatedynamic;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Fee/Update/FeeUpdateDynamic.jsx. */
@Service("page_fee_update_update_feeupdatedynamic_feeupdatedynamicservice_java")
public class FeeUpdateDynamicService extends PageArtifactService<FeeUpdateDynamicRepository> {
    public FeeUpdateDynamicService(FeeUpdateDynamicRepository repository) {
        super(repository);
    }
}