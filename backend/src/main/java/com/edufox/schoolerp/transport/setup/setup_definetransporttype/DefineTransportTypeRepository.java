package com.edufox.schoolerp.transport.setup.setup_definetransporttype;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Transport/Setup/DefineTransportType.jsx. */
@Repository("page_com_edufox_schoolerp_transport_setup_setup_definetransporttype_definetransporttyperepository_java")
public class DefineTransportTypeRepository extends PageArtifactRepository {
    public DefineTransportTypeRepository() {
        super("Transport", "setup", "DefineTransportType", "Transport/Setup/DefineTransportType.jsx");
    }
}