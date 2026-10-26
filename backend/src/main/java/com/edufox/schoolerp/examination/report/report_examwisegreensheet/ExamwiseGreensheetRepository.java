package com.edufox.schoolerp.examination.report.report_examwisegreensheet;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Report/ExamwiseGreensheet.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_report_examwisegreensheet_examwisegreensheetrepository_java")
public class ExamwiseGreensheetRepository extends PageArtifactRepository {
    public ExamwiseGreensheetRepository() {
        super("ExamMaster", "report", "ExamwiseGreensheet", "ExamMaster/Report/ExamwiseGreensheet.jsx");
    }
}