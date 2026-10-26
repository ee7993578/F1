package com.edufox.schoolerp.fee.work.work_latefeereport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Fee/Work/LateFeeReport.jsx. */
@Service("page_fee_work_work_latefeereport_latefeereportservice_java")
public class LateFeeReportService extends PageArtifactService<LateFeeReportRepository> {
    public LateFeeReportService(LateFeeReportRepository repository) {
        super(repository);
    }
}