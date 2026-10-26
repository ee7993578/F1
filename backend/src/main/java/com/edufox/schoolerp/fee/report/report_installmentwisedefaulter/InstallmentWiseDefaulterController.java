package com.edufox.schoolerp.fee.report.report_installmentwisedefaulter;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Fee/Report/InstallmentWiseDefaulter.jsx. */
@RestController("page_fee_report_report_installmentwisedefaulter_installmentwisedefaultercontroller_java")
@RequestMapping("/api/page-artifacts/fee/report/report_installmentwisedefaulter")
public class InstallmentWiseDefaulterController extends PageArtifactController<InstallmentWiseDefaulterService> {
    public InstallmentWiseDefaulterController(InstallmentWiseDefaulterService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}