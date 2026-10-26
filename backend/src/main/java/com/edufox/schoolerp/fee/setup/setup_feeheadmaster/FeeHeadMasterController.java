package com.edufox.schoolerp.fee.setup.setup_feeheadmaster;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Fee/Setup/FeeHeadMaster.jsx. */
@RestController("page_fee_setup_setup_feeheadmaster_feeheadmastercontroller_java")
@RequestMapping("/api/page-artifacts/fee/setup/setup_feeheadmaster")
public class FeeHeadMasterController extends PageArtifactController<FeeHeadMasterService> {
    public FeeHeadMasterController(FeeHeadMasterService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}