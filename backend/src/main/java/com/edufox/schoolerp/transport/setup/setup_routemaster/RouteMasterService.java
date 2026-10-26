package com.edufox.schoolerp.transport.setup.setup_routemaster;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Transport/Setup/RouteMaster.jsx. */
@Service("page_transport_setup_setup_routemaster_routemasterservice_java")
public class RouteMasterService extends PageArtifactService<RouteMasterRepository> {
    public RouteMasterService(RouteMasterRepository repository) {
        super(repository);
    }
}