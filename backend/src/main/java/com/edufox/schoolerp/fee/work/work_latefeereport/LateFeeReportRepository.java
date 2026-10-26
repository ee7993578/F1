package com.edufox.schoolerp.fee.work.work_latefeereport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Fee/Work/LateFeeReport.jsx. */
@Repository("page_com_edufox_schoolerp_fee_work_work_latefeereport_latefeereportrepository_java")
public class LateFeeReportRepository extends PageArtifactRepository {
    public LateFeeReportRepository() {
        super("Fee", "work", "LateFeeReport", "Fee/Work/LateFeeReport.jsx");
    }
}