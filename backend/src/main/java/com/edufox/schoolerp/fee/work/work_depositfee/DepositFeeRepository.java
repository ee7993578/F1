package com.edufox.schoolerp.fee.work.work_depositfee;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Fee/Work/DepositFee.jsx. */
@Repository("page_com_edufox_schoolerp_fee_work_work_depositfee_depositfeerepository_java")
public class DepositFeeRepository extends PageArtifactRepository {
    public DepositFeeRepository() {
        super("Fee", "work", "DepositFee", "Fee/Work/DepositFee.jsx");
    }
}