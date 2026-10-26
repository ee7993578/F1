package com.edufox.schoolerp.fee.report.report_studentfeesummary;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Fee/Report/StudentFeeSummary.jsx. */
@Repository("page_com_edufox_schoolerp_fee_report_report_studentfeesummary_studentfeesummaryrepository_java")
public class StudentFeeSummaryRepository extends PageArtifactRepository {
    public StudentFeeSummaryRepository() {
        super("Fee", "report", "StudentFeeSummary", "Fee/Report/StudentFeeSummary.jsx");
    }
}