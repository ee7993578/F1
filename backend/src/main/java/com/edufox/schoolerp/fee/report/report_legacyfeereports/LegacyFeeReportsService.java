package com.edufox.schoolerp.fee.report.report_legacyfeereports;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Fee/Report/LegacyFeeReports.jsx. */
@Service("page_fee_report_report_legacyfeereports_legacyfeereportsservice_java")
public class LegacyFeeReportsService extends PageArtifactService<LegacyFeeReportsRepository> {
    public LegacyFeeReportsService(LegacyFeeReportsRepository repository) {
        super(repository);
    }
}