package com.edufox.schoolerp.examination.work.work_definelearnerprofileindicator;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Work/DefineLearnerProfileIndicator.jsx. */
@Repository("page_com_edufox_schoolerp_examination_work_work_definelearnerprofileindicator_definelearnerprofileindicatorrepository_java")
public class DefineLearnerProfileIndicatorRepository extends PageArtifactRepository {
    public DefineLearnerProfileIndicatorRepository() {
        super("ExamMaster", "work", "DefineLearnerProfileIndicator", "ExamMaster/Work/DefineLearnerProfileIndicator.jsx");
    }
}