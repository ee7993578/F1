package com.edufox.schoolerp.fee.report.report_concessiongroupreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Fee/Report/ConcessionGroupReport.jsx. */
@Repository("page_com_edufox_schoolerp_fee_report_report_concessiongroupreport_concessiongroupreportrepository_java")
public class ConcessionGroupReportRepository extends PageArtifactRepository {
    public ConcessionGroupReportRepository() {
        super("Fee", "report", "ConcessionGroupReport", "Fee/Report/ConcessionGroupReport.jsx");
    }
}