package com.edufox.schoolerp.transport.setup.setup_routestoppagemapping;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Transport/Setup/RouteStoppageMapping.jsx. */
@Service("page_transport_setup_setup_routestoppagemapping_routestoppagemappingservice_java")
public class RouteStoppageMappingService extends PageArtifactService<RouteStoppageMappingRepository> {
    public RouteStoppageMappingService(RouteStoppageMappingRepository repository) {
        super(repository);
    }
}