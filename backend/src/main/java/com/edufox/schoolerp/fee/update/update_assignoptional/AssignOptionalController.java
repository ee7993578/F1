package com.edufox.schoolerp.fee.update.update_assignoptional;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Fee/Update/AssignOptional.jsx. */
@RestController("page_fee_update_update_assignoptional_assignoptionalcontroller_java")
@RequestMapping("/api/page-artifacts/fee/update/update_assignoptional")
public class AssignOptionalController extends PageArtifactController<AssignOptionalService> {
    public AssignOptionalController(AssignOptionalService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}