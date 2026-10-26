package com.edufox.schoolerp.examination.report.report_marksstatusgraph;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Report/MarksStatusGraph.jsx. */
@Service("page_examination_report_report_marksstatusgraph_marksstatusgraphservice_java")
public class MarksStatusGraphService extends PageArtifactService<MarksStatusGraphRepository> {
    public MarksStatusGraphService(MarksStatusGraphRepository repository) {
        super(repository);
    }
}