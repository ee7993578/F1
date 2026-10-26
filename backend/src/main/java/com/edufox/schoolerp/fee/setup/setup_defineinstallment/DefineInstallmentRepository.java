package com.edufox.schoolerp.fee.setup.setup_defineinstallment;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Fee/Setup/DefineInstallment.jsx. */
@Repository("page_com_edufox_schoolerp_fee_setup_setup_defineinstallment_defineinstallmentrepository_java")
public class DefineInstallmentRepository extends PageArtifactRepository {
    public DefineInstallmentRepository() {
        super("Fee", "setup", "DefineInstallment", "Fee/Setup/DefineInstallment.jsx");
    }
}