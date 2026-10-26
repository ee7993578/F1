package com.edufox.schoolerp.examination.report.report_cumulativesheet;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Report/CumulativeSheet.jsx. */
@Service("page_examination_report_report_cumulativesheet_cumulativesheetservice_java")
public class CumulativeSheetService extends PageArtifactService<CumulativeSheetRepository> {
    public CumulativeSheetService(CumulativeSheetRepository repository) {
        super(repository);
    }
}