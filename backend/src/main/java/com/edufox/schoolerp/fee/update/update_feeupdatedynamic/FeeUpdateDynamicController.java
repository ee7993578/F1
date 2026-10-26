package com.edufox.schoolerp.fee.update.update_feeupdatedynamic;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Fee/Update/FeeUpdateDynamic.jsx. */
@RestController("page_fee_update_update_feeupdatedynamic_feeupdatedynamiccontroller_java")
@RequestMapping("/api/page-artifacts/fee/update/update_feeupdatedynamic")
public class FeeUpdateDynamicController extends PageArtifactController<FeeUpdateDynamicService> {
    public FeeUpdateDynamicController(FeeUpdateDynamicService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}