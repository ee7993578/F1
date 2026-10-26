package com.edufox.schoolerp.fee.setup.setup_definelatefee;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Fee/Setup/DefineLateFee.jsx. */
@Repository("page_com_edufox_schoolerp_fee_setup_setup_definelatefee_definelatefeerepository_java")
public class DefineLateFeeRepository extends PageArtifactRepository {
    public DefineLateFeeRepository() {
        super("Fee", "setup", "DefineLateFee", "Fee/Setup/DefineLateFee.jsx");
    }
}