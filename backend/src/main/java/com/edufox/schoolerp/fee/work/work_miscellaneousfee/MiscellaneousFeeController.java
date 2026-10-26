package com.edufox.schoolerp.fee.work.work_miscellaneousfee;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Fee/Work/MiscellaneousFee.jsx. */
@RestController("page_fee_work_work_miscellaneousfee_miscellaneousfeecontroller_java")
@RequestMapping("/api/page-artifacts/fee/work/work_miscellaneousfee")
public class MiscellaneousFeeController extends PageArtifactController<MiscellaneousFeeService> {
    public MiscellaneousFeeController(MiscellaneousFeeService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}