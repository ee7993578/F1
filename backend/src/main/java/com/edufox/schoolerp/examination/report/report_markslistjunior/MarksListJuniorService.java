package com.edufox.schoolerp.examination.report.report_markslistjunior;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Report/MarksListJunior.jsx. */
@Service("page_examination_report_report_markslistjunior_markslistjuniorservice_java")
public class MarksListJuniorService extends PageArtifactService<MarksListJuniorRepository> {
    public MarksListJuniorService(MarksListJuniorRepository repository) {
        super(repository);
    }
}