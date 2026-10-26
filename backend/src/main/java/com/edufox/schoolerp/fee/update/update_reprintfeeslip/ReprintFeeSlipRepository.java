package com.edufox.schoolerp.fee.update.update_reprintfeeslip;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Fee/Update/ReprintFeeSlip.jsx. */
@Repository("page_com_edufox_schoolerp_fee_update_update_reprintfeeslip_reprintfeesliprepository_java")
public class ReprintFeeSlipRepository extends PageArtifactRepository {
    public ReprintFeeSlipRepository() {
        super("Fee", "update", "ReprintFeeSlip", "Fee/Update/ReprintFeeSlip.jsx");
    }
}