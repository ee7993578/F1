package com.edufox.schoolerp.fee.update.update_assignoptional;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Fee/Update/AssignOptional.jsx. */
@Service("page_fee_update_update_assignoptional_assignoptionalservice_java")
public class AssignOptionalService extends PageArtifactService<AssignOptionalRepository> {
    public AssignOptionalService(AssignOptionalRepository repository) {
        super(repository);
    }
}