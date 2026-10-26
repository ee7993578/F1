package com.edufox.schoolerp.transport.setup.setup_routemaster;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Transport/Setup/RouteMaster.jsx. */
@RestController("page_transport_setup_setup_routemaster_routemastercontroller_java")
@RequestMapping("/api/page-artifacts/transport/setup/setup_routemaster")
public class RouteMasterController extends PageArtifactController<RouteMasterService> {
    public RouteMasterController(RouteMasterService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}