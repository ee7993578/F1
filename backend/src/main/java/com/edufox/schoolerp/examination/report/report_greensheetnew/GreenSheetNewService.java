package com.edufox.schoolerp.examination.report.report_greensheetnew;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Report/GreenSheetNew.jsx. */
@Service("page_examination_report_report_greensheetnew_greensheetnewservice_java")
public class GreenSheetNewService extends PageArtifactService<GreenSheetNewRepository> {
    public GreenSheetNewService(GreenSheetNewRepository repository) {
        super(repository);
    }
}