package com.edufox.schoolerp.canteen.report.report_remainingproductreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Canteen/Report/RemainingProductReport.jsx. */
@RestController("page_canteen_report_report_remainingproductreport_remainingproductreportcontroller_java")
@RequestMapping("/api/page-artifacts/canteen/report/report_remainingproductreport")
public class RemainingProductReportController extends PageArtifactController<RemainingProductReportService> {
    public RemainingProductReportController(RemainingProductReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}