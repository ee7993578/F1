package com.edufox.schoolerp.fee.report.report_legacyfeedetailreports;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Fee/Report/LegacyFeeDetailReports.jsx. */
@RestController("page_fee_report_report_legacyfeedetailreports_legacyfeedetailreportscontroller_java")
@RequestMapping("/api/page-artifacts/fee/report/report_legacyfeedetailreports")
public class LegacyFeeDetailReportsController extends PageArtifactController<LegacyFeeDetailReportsService> {
    public LegacyFeeDetailReportsController(LegacyFeeDetailReportsService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}