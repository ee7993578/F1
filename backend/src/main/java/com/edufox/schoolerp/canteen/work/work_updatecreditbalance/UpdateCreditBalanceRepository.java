package com.edufox.schoolerp.canteen.work.work_updatecreditbalance;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Canteen/Work/UpdateCreditBalance.jsx. */
@Repository("page_com_edufox_schoolerp_canteen_work_work_updatecreditbalance_updatecreditbalancerepository_java")
public class UpdateCreditBalanceRepository extends PageArtifactRepository {
    public UpdateCreditBalanceRepository() {
        super("Canteen", "work", "UpdateCreditBalance", "Canteen/Work/UpdateCreditBalance.jsx");
    }
}