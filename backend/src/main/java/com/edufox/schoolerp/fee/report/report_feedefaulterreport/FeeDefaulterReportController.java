package com.edufox.schoolerp.fee.report.report_feedefaulterreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Fee/Report/FeeDefaulterReport.jsx. */
@RestController("page_fee_report_report_feedefaulterreport_feedefaulterreportcontroller_java")
@RequestMapping("/api/page-artifacts/fee/report/report_feedefaulterreport")
public class FeeDefaulterReportController extends PageArtifactController<FeeDefaulterReportService> {
    public FeeDefaulterReportController(FeeDefaulterReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}