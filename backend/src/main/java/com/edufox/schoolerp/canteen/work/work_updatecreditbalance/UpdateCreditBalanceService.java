package com.edufox.schoolerp.canteen.work.work_updatecreditbalance;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Canteen/Work/UpdateCreditBalance.jsx. */
@Service("page_canteen_work_work_updatecreditbalance_updatecreditbalanceservice_java")
public class UpdateCreditBalanceService extends PageArtifactService<UpdateCreditBalanceRepository> {
    public UpdateCreditBalanceService(UpdateCreditBalanceRepository repository) {
        super(repository);
    }
}