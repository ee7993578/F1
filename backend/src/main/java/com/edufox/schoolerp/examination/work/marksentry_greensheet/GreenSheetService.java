package com.edufox.schoolerp.examination.work.marksentry_greensheet;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/marksEntry/GreenSheet.jsx. */
@Service("page_examination_work_marksentry_greensheet_greensheetservice_java")
public class GreenSheetService extends PageArtifactService<GreenSheetRepository> {
    public GreenSheetService(GreenSheetRepository repository) {
        super(repository);
    }
}