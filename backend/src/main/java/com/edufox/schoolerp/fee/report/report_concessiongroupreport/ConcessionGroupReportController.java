package com.edufox.schoolerp.fee.report.report_concessiongroupreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Fee/Report/ConcessionGroupReport.jsx. */
@RestController("page_fee_report_report_concessiongroupreport_concessiongroupreportcontroller_java")
@RequestMapping("/api/page-artifacts/fee/report/report_concessiongroupreport")
public class ConcessionGroupReportController extends PageArtifactController<ConcessionGroupReportService> {
    public ConcessionGroupReportController(ConcessionGroupReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}