package com.edufox.schoolerp.examination.work.work_definelearnerprofileindicator;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Work/DefineLearnerProfileIndicator.jsx. */
@Service("page_examination_work_work_definelearnerprofileindicator_definelearnerprofileindicatorservice_java")
public class DefineLearnerProfileIndicatorService extends PageArtifactService<DefineLearnerProfileIndicatorRepository> {
    public DefineLearnerProfileIndicatorService(DefineLearnerProfileIndicatorRepository repository) {
        super(repository);
    }
}