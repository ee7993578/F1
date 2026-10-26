package com.edufox.schoolerp.examination.report.greensheet_examwisegreensheet;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Greensheet/ExamwiseGreensheet.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_greensheet_examwisegreensheet_examwisegreensheetrepository_java")
public class ExamwiseGreensheetRepository extends PageArtifactRepository {
    public ExamwiseGreensheetRepository() {
        super("ExamMaster", "report", "ExamwiseGreensheet", "ExamMaster/Greensheet/ExamwiseGreensheet.jsx");
    }
}