package com.edufox.schoolerp.examination.work.marksentry_defineindicatorgrade;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/marksEntry/DefineIndicatorGrade.jsx. */
@Service("page_examination_work_marksentry_defineindicatorgrade_defineindicatorgradeservice_java")
public class DefineIndicatorGradeService extends PageArtifactService<DefineIndicatorGradeRepository> {
    public DefineIndicatorGradeService(DefineIndicatorGradeRepository repository) {
        super(repository);
    }
}