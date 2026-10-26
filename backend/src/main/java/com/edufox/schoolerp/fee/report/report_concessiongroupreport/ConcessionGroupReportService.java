package com.edufox.schoolerp.fee.report.report_concessiongroupreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Fee/Report/ConcessionGroupReport.jsx. */
@Service("page_fee_report_report_concessiongroupreport_concessiongroupreportservice_java")
public class ConcessionGroupReportService extends PageArtifactService<ConcessionGroupReportRepository> {
    public ConcessionGroupReportService(ConcessionGroupReportRepository repository) {
        super(repository);
    }
}