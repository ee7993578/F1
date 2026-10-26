package com.edufox.schoolerp.transport.setup.setup_stoppagemaster;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Transport/Setup/StoppageMaster.jsx. */
@Service("page_transport_setup_setup_stoppagemaster_stoppagemasterservice_java")
public class StoppageMasterService extends PageArtifactService<StoppageMasterRepository> {
    public StoppageMasterService(StoppageMasterRepository repository) {
        super(repository);
    }
}