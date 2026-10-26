package com.edufox.schoolerp.canteen.work.work_expensenamemaster;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Canteen/Work/ExpenseNameMaster.jsx. */
@Repository("page_com_edufox_schoolerp_canteen_work_work_expensenamemaster_expensenamemasterrepository_java")
public class ExpenseNameMasterRepository extends PageArtifactRepository {
    public ExpenseNameMasterRepository() {
        super("Canteen", "work", "ExpenseNameMaster", "Canteen/Work/ExpenseNameMaster.jsx");
    }
}