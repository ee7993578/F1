package com.edufox.schoolerp.fee.report.report_concessionreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Fee/Report/ConcessionReport.jsx. */
@Service("page_fee_report_report_concessionreport_concessionreportservice_java")
public class ConcessionReportService extends PageArtifactService<ConcessionReportRepository> {
    public ConcessionReportService(ConcessionReportRepository repository) {
        super(repository);
    }
}