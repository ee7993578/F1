package com.edufox.schoolerp.examination.work.marskentrystatus_markstatusreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/marskEntryStatus/MarkStatusReport.jsx. */
@Service("page_examination_work_marskentrystatus_markstatusreport_markstatusreportservice_java")
public class MarkStatusReportService extends PageArtifactService<MarkStatusReportRepository> {
    public MarkStatusReportService(MarkStatusReportRepository repository) {
        super(repository);
    }
}