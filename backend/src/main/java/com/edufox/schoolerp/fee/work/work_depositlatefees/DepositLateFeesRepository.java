package com.edufox.schoolerp.fee.work.work_depositlatefees;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Fee/Work/DepositLateFees.jsx. */
@Repository("page_com_edufox_schoolerp_fee_work_work_depositlatefees_depositlatefeesrepository_java")
public class DepositLateFeesRepository extends PageArtifactRepository {
    public DepositLateFeesRepository() {
        super("Fee", "work", "DepositLateFees", "Fee/Work/DepositLateFees.jsx");
    }
}