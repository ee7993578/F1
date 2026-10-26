package com.edufox.schoolerp.examination.report.report_marksstatusgraph;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Report/MarksStatusGraph.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_report_marksstatusgraph_marksstatusgraphrepository_java")
public class MarksStatusGraphRepository extends PageArtifactRepository {
    public MarksStatusGraphRepository() {
        super("ExamMaster", "report", "MarksStatusGraph", "ExamMaster/Report/MarksStatusGraph.jsx");
    }
}