package com.edufox.schoolerp.canteen.work.work_expensegroupmaster;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Canteen/Work/ExpenseGroupMaster.jsx. */
@Repository("page_com_edufox_schoolerp_canteen_work_work_expensegroupmaster_expensegroupmasterrepository_java")
public class ExpenseGroupMasterRepository extends PageArtifactRepository {
    public ExpenseGroupMasterRepository() {
        super("Canteen", "work", "ExpenseGroupMaster", "Canteen/Work/ExpenseGroupMaster.jsx");
    }
}