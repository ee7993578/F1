package com.edufox.schoolerp.fee.work.work_refundablefees;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Fee/Work/RefundableFees.jsx. */
@Repository("page_com_edufox_schoolerp_fee_work_work_refundablefees_refundablefeesrepository_java")
public class RefundableFeesRepository extends PageArtifactRepository {
    public RefundableFeesRepository() {
        super("Fee", "work", "RefundableFees", "Fee/Work/RefundableFees.jsx");
    }
}