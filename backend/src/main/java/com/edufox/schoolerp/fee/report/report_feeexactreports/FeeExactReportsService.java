package com.edufox.schoolerp.fee.report.report_feeexactreports;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Fee/Report/FeeExactReports.jsx. */
@Service("page_fee_report_report_feeexactreports_feeexactreportsservice_java")
public class FeeExactReportsService extends PageArtifactService<FeeExactReportsRepository> {
    public FeeExactReportsService(FeeExactReportsRepository repository) {
        super(repository);
    }
}