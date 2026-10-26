package com.edufox.schoolerp.canteen.report.report_remainingproductreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Canteen/Report/RemainingProductReport.jsx. */
@Repository("page_com_edufox_schoolerp_canteen_report_report_remainingproductreport_remainingproductreportrepository_java")
public class RemainingProductReportRepository extends PageArtifactRepository {
    public RemainingProductReportRepository() {
        super("Canteen", "report", "RemainingProductReport", "Canteen/Report/RemainingProductReport.jsx");
    }
}