package com.edufox.schoolerp.canteen.work.work_assignexpense;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Canteen/Work/AssignExpense.jsx. */
@Repository("page_com_edufox_schoolerp_canteen_work_work_assignexpense_assignexpenserepository_java")
public class AssignExpenseRepository extends PageArtifactRepository {
    public AssignExpenseRepository() {
        super("Canteen", "work", "AssignExpense", "Canteen/Work/AssignExpense.jsx");
    }
}