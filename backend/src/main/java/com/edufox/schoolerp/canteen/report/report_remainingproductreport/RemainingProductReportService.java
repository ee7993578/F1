package com.edufox.schoolerp.canteen.report.report_remainingproductreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Canteen/Report/RemainingProductReport.jsx. */
@Service("page_canteen_report_report_remainingproductreport_remainingproductreportservice_java")
public class RemainingProductReportService extends PageArtifactService<RemainingProductReportRepository> {
    public RemainingProductReportService(RemainingProductReportRepository repository) {
        super(repository);
    }
}