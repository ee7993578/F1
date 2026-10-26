package com.edufox.schoolerp.fee.work.work_feeworkdynamic;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Fee/Work/FeeWorkDynamic.jsx. */
@Service("page_fee_work_work_feeworkdynamic_feeworkdynamicservice_java")
public class FeeWorkDynamicService extends PageArtifactService<FeeWorkDynamicRepository> {
    public FeeWorkDynamicService(FeeWorkDynamicRepository repository) {
        super(repository);
    }
}