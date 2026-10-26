package com.edufox.schoolerp.transport.setup.setup_stoppagemaster;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Transport/Setup/StoppageMaster.jsx. */
@RestController("page_transport_setup_setup_stoppagemaster_stoppagemastercontroller_java")
@RequestMapping("/api/page-artifacts/transport/setup/setup_stoppagemaster")
public class StoppageMasterController extends PageArtifactController<StoppageMasterService> {
    public StoppageMasterController(StoppageMasterService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}