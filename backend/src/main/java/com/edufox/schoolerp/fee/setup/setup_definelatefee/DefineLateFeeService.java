package com.edufox.schoolerp.fee.setup.setup_definelatefee;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Fee/Setup/DefineLateFee.jsx. */
@Service("page_fee_setup_setup_definelatefee_definelatefeeservice_java")
public class DefineLateFeeService extends PageArtifactService<DefineLateFeeRepository> {
    public DefineLateFeeService(DefineLateFeeRepository repository) {
        super(repository);
    }
}