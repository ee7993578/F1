package com.edufox.schoolerp.canteen.report.report_tuckshopreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Canteen/Report/TuckshopReport.jsx. */
@Repository("page_com_edufox_schoolerp_canteen_report_report_tuckshopreport_tuckshopreportrepository_java")
public class TuckshopReportRepository extends PageArtifactRepository {
    public TuckshopReportRepository() {
        super("Canteen", "report", "TuckshopReport", "Canteen/Report/TuckshopReport.jsx");
    }
}