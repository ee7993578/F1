package com.edufox.schoolerp.fee.update.update_feenotice;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Fee/Update/FeeNotice.jsx. */
@RestController("page_fee_update_update_feenotice_feenoticecontroller_java")
@RequestMapping("/api/page-artifacts/fee/update/update_feenotice")
public class FeeNoticeController extends PageArtifactController<FeeNoticeService> {
    public FeeNoticeController(FeeNoticeService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}