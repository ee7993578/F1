package com.edufox.schoolerp.examination.report.report_examwisegreensheet;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Report/ExamwiseGreensheet.jsx. */
@Service("page_examination_report_report_examwisegreensheet_examwisegreensheetservice_java")
public class ExamwiseGreensheetService extends PageArtifactService<ExamwiseGreensheetRepository> {
    public ExamwiseGreensheetService(ExamwiseGreensheetRepository repository) {
        super(repository);
    }
}