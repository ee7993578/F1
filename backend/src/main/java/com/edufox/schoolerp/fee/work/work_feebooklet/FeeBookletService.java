package com.edufox.schoolerp.fee.work.work_feebooklet;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Fee/Work/FeeBooklet.jsx. */
@Service("page_fee_work_work_feebooklet_feebookletservice_java")
public class FeeBookletService extends PageArtifactService<FeeBookletRepository> {
    public FeeBookletService(FeeBookletRepository repository) {
        super(repository);
    }
}