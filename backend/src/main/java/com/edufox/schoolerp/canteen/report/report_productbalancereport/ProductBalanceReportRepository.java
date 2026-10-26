package com.edufox.schoolerp.canteen.report.report_productbalancereport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Canteen/Report/ProductBalanceReport.jsx. */
@Repository("page_com_edufox_schoolerp_canteen_report_report_productbalancereport_productbalancereportrepository_java")
public class ProductBalanceReportRepository extends PageArtifactRepository {
    public ProductBalanceReportRepository() {
        super("Canteen", "report", "ProductBalanceReport", "Canteen/Report/ProductBalanceReport.jsx");
    }
}