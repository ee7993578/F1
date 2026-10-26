package com.edufox.schoolerp.examination.work.work_examworkexact;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Work/ExamWorkExact.jsx. */
@Service("page_examination_work_work_examworkexact_examworkexactservice_java")
public class ExamWorkExactService extends PageArtifactService<ExamWorkExactRepository> {
    public ExamWorkExactService(ExamWorkExactRepository repository) {
        super(repository);
    }
}