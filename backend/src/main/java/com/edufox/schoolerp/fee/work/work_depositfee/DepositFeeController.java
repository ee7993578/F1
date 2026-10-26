package com.edufox.schoolerp.fee.work.work_depositfee;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Fee/Work/DepositFee.jsx. */
@RestController("page_fee_work_work_depositfee_depositfeecontroller_java")
@RequestMapping("/api/page-artifacts/fee/work/work_depositfee")
public class DepositFeeController extends PageArtifactController<DepositFeeService> {
    public DepositFeeController(DepositFeeService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}