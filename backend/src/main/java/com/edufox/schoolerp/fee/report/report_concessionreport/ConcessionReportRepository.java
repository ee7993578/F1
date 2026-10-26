package com.edufox.schoolerp.fee.report.report_concessionreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Fee/Report/ConcessionReport.jsx. */
@Repository("page_com_edufox_schoolerp_fee_report_report_concessionreport_concessionreportrepository_java")
public class ConcessionReportRepository extends PageArtifactRepository {
    public ConcessionReportRepository() {
        super("Fee", "report", "ConcessionReport", "Fee/Report/ConcessionReport.jsx");
    }
}