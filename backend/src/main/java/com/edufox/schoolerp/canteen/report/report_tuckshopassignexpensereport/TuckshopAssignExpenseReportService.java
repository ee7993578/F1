package com.edufox.schoolerp.canteen.report.report_tuckshopassignexpensereport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Canteen/Report/TuckshopAssignExpenseReport.jsx. */
@Service("page_canteen_report_report_tuckshopassignexpensereport_tuckshopassignexpensereportservice_java")
public class TuckshopAssignExpenseReportService extends PageArtifactService<TuckshopAssignExpenseReportRepository> {
    public TuckshopAssignExpenseReportService(TuckshopAssignExpenseReportRepository repository) {
        super(repository);
    }
}