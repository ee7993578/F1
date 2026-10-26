package com.edufox.schoolerp.fee.work.work_refundablefees;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Fee/Work/RefundableFees.jsx. */
@Service("page_fee_work_work_refundablefees_refundablefeesservice_java")
public class RefundableFeesService extends PageArtifactService<RefundableFeesRepository> {
    public RefundableFeesService(RefundableFeesRepository repository) {
        super(repository);
    }
}