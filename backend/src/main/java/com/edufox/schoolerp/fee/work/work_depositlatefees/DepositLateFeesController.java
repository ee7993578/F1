package com.edufox.schoolerp.fee.work.work_depositlatefees;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Fee/Work/DepositLateFees.jsx. */
@RestController("page_fee_work_work_depositlatefees_depositlatefeescontroller_java")
@RequestMapping("/api/page-artifacts/fee/work/work_depositlatefees")
public class DepositLateFeesController extends PageArtifactController<DepositLateFeesService> {
    public DepositLateFeesController(DepositLateFeesService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}