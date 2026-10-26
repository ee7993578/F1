package com.edufox.schoolerp.examination.report.greensheet_greensheetnew;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Greensheet/GreenSheetNew.jsx. */
@Service("page_examination_report_greensheet_greensheetnew_greensheetnewservice_java")
public class GreenSheetNewService extends PageArtifactService<GreenSheetNewRepository> {
    public GreenSheetNewService(GreenSheetNewRepository repository) {
        super(repository);
    }
}