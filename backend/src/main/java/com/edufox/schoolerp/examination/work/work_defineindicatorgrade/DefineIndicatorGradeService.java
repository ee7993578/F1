package com.edufox.schoolerp.examination.work.work_defineindicatorgrade;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Work/DefineIndicatorGrade.jsx. */
@Service("page_examination_work_work_defineindicatorgrade_defineindicatorgradeservice_java")
public class DefineIndicatorGradeService extends PageArtifactService<DefineIndicatorGradeRepository> {
    public DefineIndicatorGradeService(DefineIndicatorGradeRepository repository) {
        super(repository);
    }
}