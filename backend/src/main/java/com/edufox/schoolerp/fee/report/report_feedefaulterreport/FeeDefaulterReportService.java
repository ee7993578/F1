package com.edufox.schoolerp.fee.report.report_feedefaulterreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Fee/Report/FeeDefaulterReport.jsx. */
@Service("page_fee_report_report_feedefaulterreport_feedefaulterreportservice_java")
public class FeeDefaulterReportService extends PageArtifactService<FeeDefaulterReportRepository> {
    public FeeDefaulterReportService(FeeDefaulterReportRepository repository) {
        super(repository);
    }
}