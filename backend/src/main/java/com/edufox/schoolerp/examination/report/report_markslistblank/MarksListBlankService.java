package com.edufox.schoolerp.examination.report.report_markslistblank;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Report/MarksListBlank.jsx. */
@Service("page_examination_report_report_markslistblank_markslistblankservice_java")
public class MarksListBlankService extends PageArtifactService<MarksListBlankRepository> {
    public MarksListBlankService(MarksListBlankRepository repository) {
        super(repository);
    }
}