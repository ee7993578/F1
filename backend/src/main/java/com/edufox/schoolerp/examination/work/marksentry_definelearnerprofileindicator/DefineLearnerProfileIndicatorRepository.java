package com.edufox.schoolerp.examination.work.marksentry_definelearnerprofileindicator;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/marksEntry/DefineLearnerProfileIndicator.jsx. */
@Repository("page_com_edufox_schoolerp_examination_work_marksentry_definelearnerprofileindicator_definelearnerprofileindicatorrepository_java")
public class DefineLearnerProfileIndicatorRepository extends PageArtifactRepository {
    public DefineLearnerProfileIndicatorRepository() {
        super("ExamMaster", "work", "DefineLearnerProfileIndicator", "ExamMaster/marksEntry/DefineLearnerProfileIndicator.jsx");
    }
}