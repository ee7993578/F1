package com.edufox.schoolerp.examination.report.reportanalytics_marksstatusgraph;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/ReportAnalytics/MarksStatusGraph.jsx. */
@Service("page_examination_report_reportanalytics_marksstatusgraph_marksstatusgraphservice_java")
public class MarksStatusGraphService extends PageArtifactService<MarksStatusGraphRepository> {
    public MarksStatusGraphService(MarksStatusGraphRepository repository) {
        super(repository);
    }
}