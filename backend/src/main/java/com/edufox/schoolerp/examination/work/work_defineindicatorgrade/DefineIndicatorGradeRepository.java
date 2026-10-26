package com.edufox.schoolerp.examination.work.work_defineindicatorgrade;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Work/DefineIndicatorGrade.jsx. */
@Repository("page_com_edufox_schoolerp_examination_work_work_defineindicatorgrade_defineindicatorgraderepository_java")
public class DefineIndicatorGradeRepository extends PageArtifactRepository {
    public DefineIndicatorGradeRepository() {
        super("ExamMaster", "work", "DefineIndicatorGrade", "ExamMaster/Work/DefineIndicatorGrade.jsx");
    }
}