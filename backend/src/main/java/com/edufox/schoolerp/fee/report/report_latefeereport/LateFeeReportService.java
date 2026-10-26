package com.edufox.schoolerp.fee.report.report_latefeereport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Fee/Report/LateFeeReport.jsx. */
@Service("page_fee_report_report_latefeereport_latefeereportservice_java")
public class LateFeeReportService extends PageArtifactService<LateFeeReportRepository> {
    public LateFeeReportService(LateFeeReportRepository repository) {
        super(repository);
    }
}