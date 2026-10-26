package com.edufox.schoolerp.examination.work.work_greensheet;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Work/GreenSheet.jsx. */
@Service("page_examination_work_work_greensheet_greensheetservice_java")
public class GreenSheetService extends PageArtifactService<GreenSheetRepository> {
    public GreenSheetService(GreenSheetRepository repository) {
        super(repository);
    }
}