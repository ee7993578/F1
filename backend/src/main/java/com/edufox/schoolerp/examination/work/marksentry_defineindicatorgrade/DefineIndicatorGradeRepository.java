package com.edufox.schoolerp.examination.work.marksentry_defineindicatorgrade;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/marksEntry/DefineIndicatorGrade.jsx. */
@Repository("page_com_edufox_schoolerp_examination_work_marksentry_defineindicatorgrade_defineindicatorgraderepository_java")
public class DefineIndicatorGradeRepository extends PageArtifactRepository {
    public DefineIndicatorGradeRepository() {
        super("ExamMaster", "work", "DefineIndicatorGrade", "ExamMaster/marksEntry/DefineIndicatorGrade.jsx");
    }
}