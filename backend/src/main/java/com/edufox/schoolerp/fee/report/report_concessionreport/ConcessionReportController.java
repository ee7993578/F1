package com.edufox.schoolerp.fee.report.report_concessionreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Fee/Report/ConcessionReport.jsx. */
@RestController("page_fee_report_report_concessionreport_concessionreportcontroller_java")
@RequestMapping("/api/page-artifacts/fee/report/report_concessionreport")
public class ConcessionReportController extends PageArtifactController<ConcessionReportService> {
    public ConcessionReportController(ConcessionReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}