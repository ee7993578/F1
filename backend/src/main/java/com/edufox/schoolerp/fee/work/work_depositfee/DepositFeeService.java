package com.edufox.schoolerp.fee.work.work_depositfee;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Fee/Work/DepositFee.jsx. */
@Service("page_fee_work_work_depositfee_depositfeeservice_java")
public class DepositFeeService extends PageArtifactService<DepositFeeRepository> {
    public DepositFeeService(DepositFeeRepository repository) {
        super(repository);
    }
}