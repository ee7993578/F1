package com.edufox.schoolerp.examination.report.marklistreports_markslistjunior;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/MarkListReports/MarksListJunior.jsx. */
@Service("page_examination_report_marklistreports_markslistjunior_markslistjuniorservice_java")
public class MarksListJuniorService extends PageArtifactService<MarksListJuniorRepository> {
    public MarksListJuniorService(MarksListJuniorRepository repository) {
        super(repository);
    }
}