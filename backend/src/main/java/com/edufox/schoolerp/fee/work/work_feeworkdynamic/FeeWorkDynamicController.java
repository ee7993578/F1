package com.edufox.schoolerp.fee.work.work_feeworkdynamic;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Fee/Work/FeeWorkDynamic.jsx. */
@RestController("page_fee_work_work_feeworkdynamic_feeworkdynamiccontroller_java")
@RequestMapping("/api/page-artifacts/fee/work/work_feeworkdynamic")
public class FeeWorkDynamicController extends PageArtifactController<FeeWorkDynamicService> {
    public FeeWorkDynamicController(FeeWorkDynamicService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}