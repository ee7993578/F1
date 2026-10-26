package com.edufox.schoolerp.fee.update.update_reconfee;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Fee/Update/ReconFee.jsx. */
@Service("page_fee_update_update_reconfee_reconfeeservice_java")
public class ReconFeeService extends PageArtifactService<ReconFeeRepository> {
    public ReconFeeService(ReconFeeRepository repository) {
        super(repository);
    }
}