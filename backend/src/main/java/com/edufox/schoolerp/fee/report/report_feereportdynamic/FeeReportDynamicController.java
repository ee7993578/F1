package com.edufox.schoolerp.fee.report.report_feereportdynamic;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Fee/Report/FeeReportDynamic.jsx. */
@RestController("page_fee_report_report_feereportdynamic_feereportdynamiccontroller_java")
@RequestMapping("/api/page-artifacts/fee/report/report_feereportdynamic")
public class FeeReportDynamicController extends PageArtifactController<FeeReportDynamicService> {
    public FeeReportDynamicController(FeeReportDynamicService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}