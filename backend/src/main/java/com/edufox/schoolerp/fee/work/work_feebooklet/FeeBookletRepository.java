package com.edufox.schoolerp.fee.work.work_feebooklet;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Fee/Work/FeeBooklet.jsx. */
@Repository("page_com_edufox_schoolerp_fee_work_work_feebooklet_feebookletrepository_java")
public class FeeBookletRepository extends PageArtifactRepository {
    public FeeBookletRepository() {
        super("Fee", "work", "FeeBooklet", "Fee/Work/FeeBooklet.jsx");
    }
}