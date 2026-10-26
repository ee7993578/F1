package com.edufox.schoolerp.fee.work.work_depositlatefees;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Fee/Work/DepositLateFees.jsx. */
@Service("page_fee_work_work_depositlatefees_depositlatefeesservice_java")
public class DepositLateFeesService extends PageArtifactService<DepositLateFeesRepository> {
    public DepositLateFeesService(DepositLateFeesRepository repository) {
        super(repository);
    }
}