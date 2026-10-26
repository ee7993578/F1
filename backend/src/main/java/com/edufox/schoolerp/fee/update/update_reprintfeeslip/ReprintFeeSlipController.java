package com.edufox.schoolerp.fee.update.update_reprintfeeslip;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Fee/Update/ReprintFeeSlip.jsx. */
@RestController("page_fee_update_update_reprintfeeslip_reprintfeeslipcontroller_java")
@RequestMapping("/api/page-artifacts/fee/update/update_reprintfeeslip")
public class ReprintFeeSlipController extends PageArtifactController<ReprintFeeSlipService> {
    public ReprintFeeSlipController(ReprintFeeSlipService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}