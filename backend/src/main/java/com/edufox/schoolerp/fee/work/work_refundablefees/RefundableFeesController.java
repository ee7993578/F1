package com.edufox.schoolerp.fee.work.work_refundablefees;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Fee/Work/RefundableFees.jsx. */
@RestController("page_fee_work_work_refundablefees_refundablefeescontroller_java")
@RequestMapping("/api/page-artifacts/fee/work/work_refundablefees")
public class RefundableFeesController extends PageArtifactController<RefundableFeesService> {
    public RefundableFeesController(RefundableFeesService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}