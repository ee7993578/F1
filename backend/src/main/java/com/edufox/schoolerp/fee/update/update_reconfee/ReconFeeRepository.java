package com.edufox.schoolerp.fee.update.update_reconfee;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Fee/Update/ReconFee.jsx. */
@Repository("page_com_edufox_schoolerp_fee_update_update_reconfee_reconfeerepository_java")
public class ReconFeeRepository extends PageArtifactRepository {
    public ReconFeeRepository() {
        super("Fee", "update", "ReconFee", "Fee/Update/ReconFee.jsx");
    }
}