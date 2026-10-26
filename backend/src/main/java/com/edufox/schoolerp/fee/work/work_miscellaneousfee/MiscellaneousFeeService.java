package com.edufox.schoolerp.fee.work.work_miscellaneousfee;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Fee/Work/MiscellaneousFee.jsx. */
@Service("page_fee_work_work_miscellaneousfee_miscellaneousfeeservice_java")
public class MiscellaneousFeeService extends PageArtifactService<MiscellaneousFeeRepository> {
    public MiscellaneousFeeService(MiscellaneousFeeRepository repository) {
        super(repository);
    }
}