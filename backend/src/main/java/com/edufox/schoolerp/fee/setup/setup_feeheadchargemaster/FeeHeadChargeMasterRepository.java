package com.edufox.schoolerp.fee.setup.setup_feeheadchargemaster;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Fee/Setup/FeeHeadChargeMaster.jsx. */
@Repository("page_com_edufox_schoolerp_fee_setup_setup_feeheadchargemaster_feeheadchargemasterrepository_java")
public class FeeHeadChargeMasterRepository extends PageArtifactRepository {
    public FeeHeadChargeMasterRepository() {
        super("Fee", "setup", "FeeHeadChargeMaster", "Fee/Setup/FeeHeadChargeMaster.jsx");
    }
}