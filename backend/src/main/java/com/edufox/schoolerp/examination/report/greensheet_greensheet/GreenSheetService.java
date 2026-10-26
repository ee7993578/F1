package com.edufox.schoolerp.examination.report.greensheet_greensheet;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Greensheet/GreenSheet.jsx. */
@Service("page_examination_report_greensheet_greensheet_greensheetservice_java")
public class GreenSheetService extends PageArtifactService<GreenSheetRepository> {
    public GreenSheetService(GreenSheetRepository repository) {
        super(repository);
    }
}