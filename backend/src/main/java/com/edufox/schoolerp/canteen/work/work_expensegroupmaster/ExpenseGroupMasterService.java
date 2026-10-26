package com.edufox.schoolerp.canteen.work.work_expensegroupmaster;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Canteen/Work/ExpenseGroupMaster.jsx. */
@Service("page_canteen_work_work_expensegroupmaster_expensegroupmasterservice_java")
public class ExpenseGroupMasterService extends PageArtifactService<ExpenseGroupMasterRepository> {
    public ExpenseGroupMasterService(ExpenseGroupMasterRepository repository) {
        super(repository);
    }
}