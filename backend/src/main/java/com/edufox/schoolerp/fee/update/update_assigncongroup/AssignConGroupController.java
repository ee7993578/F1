package com.edufox.schoolerp.fee.update.update_assigncongroup;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Fee/Update/AssignConGroup.jsx. */
@RestController("page_fee_update_update_assigncongroup_assigncongroupcontroller_java")
@RequestMapping("/api/page-artifacts/fee/update/update_assigncongroup")
public class AssignConGroupController extends PageArtifactController<AssignConGroupService> {
    public AssignConGroupController(AssignConGroupService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}