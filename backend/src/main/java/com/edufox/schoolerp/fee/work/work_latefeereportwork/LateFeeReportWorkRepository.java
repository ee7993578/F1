package com.edufox.schoolerp.fee.work.work_latefeereportwork;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Fee/Work/LateFeeReportWork.jsx. */
@Repository("page_com_edufox_schoolerp_fee_work_work_latefeereportwork_latefeereportworkrepository_java")
public class LateFeeReportWorkRepository extends PageArtifactRepository {
    public LateFeeReportWorkRepository() {
        super("Fee", "work", "LateFeeReportWork", "Fee/Work/LateFeeReportWork.jsx");
    }
}