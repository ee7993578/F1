package com.edufox.schoolerp.canteen.report.report_productbalancereport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Canteen/Report/ProductBalanceReport.jsx. */
@Service("page_canteen_report_report_productbalancereport_productbalancereportservice_java")
public class ProductBalanceReportService extends PageArtifactService<ProductBalanceReportRepository> {
    public ProductBalanceReportService(ProductBalanceReportRepository repository) {
        super(repository);
    }
}