package com.edufox.schoolerp.fee.work.work_feesettlement;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Fee/Work/FeeSettlement.jsx. */
@RestController("page_fee_work_work_feesettlement_feesettlementcontroller_java")
@RequestMapping("/api/page-artifacts/fee/work/work_feesettlement")
public class FeeSettlementController extends PageArtifactController<FeeSettlementService> {
    public FeeSettlementController(FeeSettlementService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}