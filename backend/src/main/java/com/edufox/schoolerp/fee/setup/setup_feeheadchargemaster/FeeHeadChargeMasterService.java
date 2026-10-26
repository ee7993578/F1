package com.edufox.schoolerp.fee.setup.setup_feeheadchargemaster;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Fee/Setup/FeeHeadChargeMaster.jsx. */
@Service("page_fee_setup_setup_feeheadchargemaster_feeheadchargemasterservice_java")
public class FeeHeadChargeMasterService extends PageArtifactService<FeeHeadChargeMasterRepository> {
    public FeeHeadChargeMasterService(FeeHeadChargeMasterRepository repository) {
        super(repository);
    }
}