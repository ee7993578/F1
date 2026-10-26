package com.edufox.schoolerp.canteen.report.report_tuckshopreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Canteen/Report/TuckshopReport.jsx. */
@Service("page_canteen_report_report_tuckshopreport_tuckshopreportservice_java")
public class TuckshopReportService extends PageArtifactService<TuckshopReportRepository> {
    public TuckshopReportService(TuckshopReportRepository repository) {
        super(repository);
    }
}