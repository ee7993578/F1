package com.edufox.schoolerp.fee.report.report_legacyfeedetailreports;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Fee/Report/LegacyFeeDetailReports.jsx. */
@Service("page_fee_report_report_legacyfeedetailreports_legacyfeedetailreportsservice_java")
public class LegacyFeeDetailReportsService extends PageArtifactService<LegacyFeeDetailReportsRepository> {
    public LegacyFeeDetailReportsService(LegacyFeeDetailReportsRepository repository) {
        super(repository);
    }
}