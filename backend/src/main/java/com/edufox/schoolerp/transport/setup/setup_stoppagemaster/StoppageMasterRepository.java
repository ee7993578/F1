package com.edufox.schoolerp.transport.setup.setup_stoppagemaster;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Transport/Setup/StoppageMaster.jsx. */
@Repository("page_com_edufox_schoolerp_transport_setup_setup_stoppagemaster_stoppagemasterrepository_java")
public class StoppageMasterRepository extends PageArtifactRepository {
    public StoppageMasterRepository() {
        super("Transport", "setup", "StoppageMaster", "Transport/Setup/StoppageMaster.jsx");
    }
}