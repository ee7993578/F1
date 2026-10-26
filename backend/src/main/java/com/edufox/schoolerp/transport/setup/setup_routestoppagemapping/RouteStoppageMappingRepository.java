package com.edufox.schoolerp.transport.setup.setup_routestoppagemapping;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Transport/Setup/RouteStoppageMapping.jsx. */
@Repository("page_com_edufox_schoolerp_transport_setup_setup_routestoppagemapping_routestoppagemappingrepository_java")
public class RouteStoppageMappingRepository extends PageArtifactRepository {
    public RouteStoppageMappingRepository() {
        super("Transport", "setup", "RouteStoppageMapping", "Transport/Setup/RouteStoppageMapping.jsx");
    }
}