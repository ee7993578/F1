package com.edufox.schoolerp.fee.work.work_feesettlement;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Fee/Work/FeeSettlement.jsx. */
@Repository("page_com_edufox_schoolerp_fee_work_work_feesettlement_feesettlementrepository_java")
public class FeeSettlementRepository extends PageArtifactRepository {
    public FeeSettlementRepository() {
        super("Fee", "work", "FeeSettlement", "Fee/Work/FeeSettlement.jsx");
    }
}