package com.edufox.schoolerp.canteen.report.report_tuckshopassignexpensereport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Canteen/Report/TuckshopAssignExpenseReport.jsx. */
@RestController("page_canteen_report_report_tuckshopassignexpensereport_tuckshopassignexpensereportcontroller_java")
@RequestMapping("/api/page-artifacts/canteen/report/report_tuckshopassignexpensereport")
public class TuckshopAssignExpenseReportController extends PageArtifactController<TuckshopAssignExpenseReportService> {
    public TuckshopAssignExpenseReportController(TuckshopAssignExpenseReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}