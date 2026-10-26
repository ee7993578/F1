package com.edufox.schoolerp.examination.report.report_greensheetsubjectwise;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Report/GreenSheetSubjectWise.jsx. */
@Service("page_examination_report_report_greensheetsubjectwise_greensheetsubjectwiseservice_java")
public class GreenSheetSubjectWiseService extends PageArtifactService<GreenSheetSubjectWiseRepository> {
    public GreenSheetSubjectWiseService(GreenSheetSubjectWiseRepository repository) {
        super(repository);
    }
}