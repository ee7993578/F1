package com.edufox.schoolerp.fee.work.work_miscellaneousfee;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Fee/Work/MiscellaneousFee.jsx. */
@Repository("page_com_edufox_schoolerp_fee_work_work_miscellaneousfee_miscellaneousfeerepository_java")
public class MiscellaneousFeeRepository extends PageArtifactRepository {
    public MiscellaneousFeeRepository() {
        super("Fee", "work", "MiscellaneousFee", "Fee/Work/MiscellaneousFee.jsx");
    }
}