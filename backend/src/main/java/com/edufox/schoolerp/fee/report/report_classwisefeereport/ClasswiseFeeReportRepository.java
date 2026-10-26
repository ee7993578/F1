package com.edufox.schoolerp.fee.report.report_classwisefeereport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Fee/Report/ClasswiseFeeReport.jsx. */
@Repository("page_com_edufox_schoolerp_fee_report_report_classwisefeereport_classwisefeereportrepository_java")
public class ClasswiseFeeReportRepository extends PageArtifactRepository {
    public ClasswiseFeeReportRepository() {
        super("Fee", "report", "ClasswiseFeeReport", "Fee/Report/ClasswiseFeeReport.jsx");
    }
}