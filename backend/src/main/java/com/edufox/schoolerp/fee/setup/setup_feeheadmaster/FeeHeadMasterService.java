package com.edufox.schoolerp.fee.setup.setup_feeheadmaster;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Fee/Setup/FeeHeadMaster.jsx. */
@Service("page_fee_setup_setup_feeheadmaster_feeheadmasterservice_java")
public class FeeHeadMasterService extends PageArtifactService<FeeHeadMasterRepository> {
    public FeeHeadMasterService(FeeHeadMasterRepository repository) {
        super(repository);
    }
}