package com.edufox.schoolerp.fee.work.work_feesettlement;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Fee/Work/FeeSettlement.jsx. */
@Service("page_fee_work_work_feesettlement_feesettlementservice_java")
public class FeeSettlementService extends PageArtifactService<FeeSettlementRepository> {
    public FeeSettlementService(FeeSettlementRepository repository) {
        super(repository);
    }
}