package com.edufox.schoolerp.transport.setup.setup_definetransporttype;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Transport/Setup/DefineTransportType.jsx. */
@RestController("page_transport_setup_setup_definetransporttype_definetransporttypecontroller_java")
@RequestMapping("/api/page-artifacts/transport/setup/setup_definetransporttype")
public class DefineTransportTypeController extends PageArtifactController<DefineTransportTypeService> {
    public DefineTransportTypeController(DefineTransportTypeService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}