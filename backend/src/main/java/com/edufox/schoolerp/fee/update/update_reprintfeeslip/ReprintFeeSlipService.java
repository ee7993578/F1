package com.edufox.schoolerp.fee.update.update_reprintfeeslip;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Fee/Update/ReprintFeeSlip.jsx. */
@Service("page_fee_update_update_reprintfeeslip_reprintfeeslipservice_java")
public class ReprintFeeSlipService extends PageArtifactService<ReprintFeeSlipRepository> {
    public ReprintFeeSlipService(ReprintFeeSlipRepository repository) {
        super(repository);
    }
}