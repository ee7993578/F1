package com.edufox.schoolerp.examination.report.greensheet_greensheetprimary;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Greensheet/GreenSheetPrimary.jsx. */
@Service("page_examination_report_greensheet_greensheetprimary_greensheetprimaryservice_java")
public class GreenSheetPrimaryService extends PageArtifactService<GreenSheetPrimaryRepository> {
    public GreenSheetPrimaryService(GreenSheetPrimaryRepository repository) {
        super(repository);
    }
}