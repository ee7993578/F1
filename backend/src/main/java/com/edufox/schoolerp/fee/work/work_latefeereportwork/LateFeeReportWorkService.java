package com.edufox.schoolerp.fee.work.work_latefeereportwork;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Fee/Work/LateFeeReportWork.jsx. */
@Service("page_fee_work_work_latefeereportwork_latefeereportworkservice_java")
public class LateFeeReportWorkService extends PageArtifactService<LateFeeReportWorkRepository> {
    public LateFeeReportWorkService(LateFeeReportWorkRepository repository) {
        super(repository);
    }
}