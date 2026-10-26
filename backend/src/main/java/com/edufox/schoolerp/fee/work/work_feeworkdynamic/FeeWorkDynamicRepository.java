package com.edufox.schoolerp.fee.work.work_feeworkdynamic;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Fee/Work/FeeWorkDynamic.jsx. */
@Repository("page_com_edufox_schoolerp_fee_work_work_feeworkdynamic_feeworkdynamicrepository_java")
public class FeeWorkDynamicRepository extends PageArtifactRepository {
    public FeeWorkDynamicRepository() {
        super("Fee", "work", "FeeWorkDynamic", "Fee/Work/FeeWorkDynamic.jsx");
    }
}