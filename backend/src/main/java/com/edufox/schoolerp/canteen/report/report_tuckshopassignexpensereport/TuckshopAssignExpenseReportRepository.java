package com.edufox.schoolerp.canteen.report.report_tuckshopassignexpensereport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Canteen/Report/TuckshopAssignExpenseReport.jsx. */
@Repository("page_com_edufox_schoolerp_canteen_report_report_tuckshopassignexpensereport_tuckshopassignexpensereportrepository_java")
public class TuckshopAssignExpenseReportRepository extends PageArtifactRepository {
    public TuckshopAssignExpenseReportRepository() {
        super("Canteen", "report", "TuckshopAssignExpenseReport", "Canteen/Report/TuckshopAssignExpenseReport.jsx");
    }
}