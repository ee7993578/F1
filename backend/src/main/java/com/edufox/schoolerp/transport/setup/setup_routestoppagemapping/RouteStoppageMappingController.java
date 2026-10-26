package com.edufox.schoolerp.transport.setup.setup_routestoppagemapping;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Transport/Setup/RouteStoppageMapping.jsx. */
@RestController("page_transport_setup_setup_routestoppagemapping_routestoppagemappingcontroller_java")
@RequestMapping("/api/page-artifacts/transport/setup/setup_routestoppagemapping")
public class RouteStoppageMappingController extends PageArtifactController<RouteStoppageMappingService> {
    public RouteStoppageMappingController(RouteStoppageMappingService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}