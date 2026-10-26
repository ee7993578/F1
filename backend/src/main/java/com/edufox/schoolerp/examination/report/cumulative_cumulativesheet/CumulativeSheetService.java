package com.edufox.schoolerp.examination.report.cumulative_cumulativesheet;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/cumulative/CumulativeSheet.jsx. */
@Service("page_examination_report_cumulative_cumulativesheet_cumulativesheetservice_java")
public class CumulativeSheetService extends PageArtifactService<CumulativeSheetRepository> {
    public CumulativeSheetService(CumulativeSheetRepository repository) {
        super(repository);
    }
}