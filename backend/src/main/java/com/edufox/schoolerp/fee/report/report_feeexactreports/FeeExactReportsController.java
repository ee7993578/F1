package com.edufox.schoolerp.fee.report.report_feeexactreports;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Fee/Report/FeeExactReports.jsx. */
@RestController("page_fee_report_report_feeexactreports_feeexactreportscontroller_java")
@RequestMapping("/api/page-artifacts/fee/report/report_feeexactreports")
public class FeeExactReportsController extends PageArtifactController<FeeExactReportsService> {
    public FeeExactReportsController(FeeExactReportsService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}