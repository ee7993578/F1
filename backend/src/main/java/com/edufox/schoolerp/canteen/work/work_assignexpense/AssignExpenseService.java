package com.edufox.schoolerp.canteen.work.work_assignexpense;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Canteen/Work/AssignExpense.jsx. */
@Service("page_canteen_work_work_assignexpense_assignexpenseservice_java")
public class AssignExpenseService extends PageArtifactService<AssignExpenseRepository> {
    public AssignExpenseService(AssignExpenseRepository repository) {
        super(repository);
    }
}