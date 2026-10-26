package com.edufox.schoolerp.fee.report.report_dailycollectionreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Fee/Report/DailyCollectionReport.jsx. */
@RestController("page_fee_report_report_dailycollectionreport_dailycollectionreportcontroller_java")
@RequestMapping("/api/page-artifacts/fee/report/report_dailycollectionreport")
public class DailyCollectionReportController extends PageArtifactController<DailyCollectionReportService> {
    public DailyCollectionReportController(DailyCollectionReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}