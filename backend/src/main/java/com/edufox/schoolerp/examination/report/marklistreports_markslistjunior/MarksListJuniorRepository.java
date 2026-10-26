package com.edufox.schoolerp.examination.report.marklistreports_markslistjunior;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/MarkListReports/MarksListJunior.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_marklistreports_markslistjunior_markslistjuniorrepository_java")
public class MarksListJuniorRepository extends PageArtifactRepository {
    public MarksListJuniorRepository() {
        super("ExamMaster", "report", "MarksListJunior", "ExamMaster/MarkListReports/MarksListJunior.jsx");
    }
}