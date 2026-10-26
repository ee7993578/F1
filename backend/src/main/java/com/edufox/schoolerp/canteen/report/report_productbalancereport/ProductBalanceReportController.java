package com.edufox.schoolerp.canteen.report.report_productbalancereport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Canteen/Report/ProductBalanceReport.jsx. */
@RestController("page_canteen_report_report_productbalancereport_productbalancereportcontroller_java")
@RequestMapping("/api/page-artifacts/canteen/report/report_productbalancereport")
public class ProductBalanceReportController extends PageArtifactController<ProductBalanceReportService> {
    public ProductBalanceReportController(ProductBalanceReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}