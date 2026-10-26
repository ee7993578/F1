package com.edufox.schoolerp.examination.report.marklistreports_markslistblank;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/MarkListReports/MarksListBlank.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_marklistreports_markslistblank_markslistblankrepository_java")
public class MarksListBlankRepository extends PageArtifactRepository {
    public MarksListBlankRepository() {
        super("ExamMaster", "report", "MarksListBlank", "ExamMaster/MarkListReports/MarksListBlank.jsx");
    }
}