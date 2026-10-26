package com.edufox.schoolerp.fee.setup.setup_defineconcessiongroup;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Fee/Setup/DefineConcessionGroup.jsx. */
@RestController("page_fee_setup_setup_defineconcessiongroup_defineconcessiongroupcontroller_java")
@RequestMapping("/api/page-artifacts/fee/setup/setup_defineconcessiongroup")
public class DefineConcessionGroupController extends PageArtifactController<DefineConcessionGroupService> {
    public DefineConcessionGroupController(DefineConcessionGroupService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}