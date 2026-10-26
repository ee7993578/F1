package com.edufox.schoolerp.fee.setup.setup_feeheadmaster;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Fee/Setup/FeeHeadMaster.jsx. */
@Repository("page_com_edufox_schoolerp_fee_setup_setup_feeheadmaster_feeheadmasterrepository_java")
public class FeeHeadMasterRepository extends PageArtifactRepository {
    public FeeHeadMasterRepository() {
        super("Fee", "setup", "FeeHeadMaster", "Fee/Setup/FeeHeadMaster.jsx");
    }
}