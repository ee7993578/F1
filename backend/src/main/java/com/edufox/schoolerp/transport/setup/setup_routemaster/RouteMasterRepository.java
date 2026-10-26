package com.edufox.schoolerp.transport.setup.setup_routemaster;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Transport/Setup/RouteMaster.jsx. */
@Repository("page_com_edufox_schoolerp_transport_setup_setup_routemaster_routemasterrepository_java")
public class RouteMasterRepository extends PageArtifactRepository {
    public RouteMasterRepository() {
        super("Transport", "setup", "RouteMaster", "Transport/Setup/RouteMaster.jsx");
    }
}