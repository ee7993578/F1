package com.edufox.schoolerp.examination.report.report_markslistjunior;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Report/MarksListJunior.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_report_markslistjunior_markslistjuniorrepository_java")
public class MarksListJuniorRepository extends PageArtifactRepository {
    public MarksListJuniorRepository() {
        super("ExamMaster", "report", "MarksListJunior", "ExamMaster/Report/MarksListJunior.jsx");
    }
}