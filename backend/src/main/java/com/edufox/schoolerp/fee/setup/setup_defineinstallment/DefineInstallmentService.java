package com.edufox.schoolerp.fee.setup.setup_defineinstallment;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Fee/Setup/DefineInstallment.jsx. */
@Service("page_fee_setup_setup_defineinstallment_defineinstallmentservice_java")
public class DefineInstallmentService extends PageArtifactService<DefineInstallmentRepository> {
    public DefineInstallmentService(DefineInstallmentRepository repository) {
        super(repository);
    }
}