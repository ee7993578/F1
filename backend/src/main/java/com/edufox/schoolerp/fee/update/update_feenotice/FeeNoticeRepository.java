package com.edufox.schoolerp.fee.update.update_feenotice;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Fee/Update/FeeNotice.jsx. */
@Repository("page_com_edufox_schoolerp_fee_update_update_feenotice_feenoticerepository_java")
public class FeeNoticeRepository extends PageArtifactRepository {
    public FeeNoticeRepository() {
        super("Fee", "update", "FeeNotice", "Fee/Update/FeeNotice.jsx");
    }
}