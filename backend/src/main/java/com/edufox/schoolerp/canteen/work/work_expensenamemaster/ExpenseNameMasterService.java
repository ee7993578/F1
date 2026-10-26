package com.edufox.schoolerp.canteen.work.work_expensenamemaster;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Canteen/Work/ExpenseNameMaster.jsx. */
@Service("page_canteen_work_work_expensenamemaster_expensenamemasterservice_java")
public class ExpenseNameMasterService extends PageArtifactService<ExpenseNameMasterRepository> {
    public ExpenseNameMasterService(ExpenseNameMasterRepository repository) {
        super(repository);
    }
}