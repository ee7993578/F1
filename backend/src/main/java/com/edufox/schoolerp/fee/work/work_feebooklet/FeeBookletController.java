package com.edufox.schoolerp.fee.work.work_feebooklet;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Fee/Work/FeeBooklet.jsx. */
@RestController("page_fee_work_work_feebooklet_feebookletcontroller_java")
@RequestMapping("/api/page-artifacts/fee/work/work_feebooklet")
public class FeeBookletController extends PageArtifactController<FeeBookletService> {
    public FeeBookletController(FeeBookletService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}