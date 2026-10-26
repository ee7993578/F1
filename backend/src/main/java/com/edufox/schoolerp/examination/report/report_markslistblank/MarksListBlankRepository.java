package com.edufox.schoolerp.examination.report.report_markslistblank;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Report/MarksListBlank.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_report_markslistblank_markslistblankrepository_java")
public class MarksListBlankRepository extends PageArtifactRepository {
    public MarksListBlankRepository() {
        super("ExamMaster", "report", "MarksListBlank", "ExamMaster/Report/MarksListBlank.jsx");
    }
}