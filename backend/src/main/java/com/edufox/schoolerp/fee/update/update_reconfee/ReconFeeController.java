package com.edufox.schoolerp.fee.update.update_reconfee;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Fee/Update/ReconFee.jsx. */
@RestController("page_fee_update_update_reconfee_reconfeecontroller_java")
@RequestMapping("/api/page-artifacts/fee/update/update_reconfee")
public class ReconFeeController extends PageArtifactController<ReconFeeService> {
    public ReconFeeController(ReconFeeService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}