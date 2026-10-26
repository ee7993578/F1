package com.edufox.schoolerp.examination.report.reportanalytics_marksstatusgraph;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/ReportAnalytics/MarksStatusGraph.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_reportanalytics_marksstatusgraph_marksstatusgraphrepository_java")
public class MarksStatusGraphRepository extends PageArtifactRepository {
    public MarksStatusGraphRepository() {
        super("ExamMaster", "report", "MarksStatusGraph", "ExamMaster/ReportAnalytics/MarksStatusGraph.jsx");
    }
}