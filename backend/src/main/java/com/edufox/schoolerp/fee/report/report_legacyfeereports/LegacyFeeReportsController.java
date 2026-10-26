package com.edufox.schoolerp.fee.report.report_legacyfeereports;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Fee/Report/LegacyFeeReports.jsx. */
@RestController("page_fee_report_report_legacyfeereports_legacyfeereportscontroller_java")
@RequestMapping("/api/page-artifacts/fee/report/report_legacyfeereports")
public class LegacyFeeReportsController extends PageArtifactController<LegacyFeeReportsService> {
    public LegacyFeeReportsController(LegacyFeeReportsService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}