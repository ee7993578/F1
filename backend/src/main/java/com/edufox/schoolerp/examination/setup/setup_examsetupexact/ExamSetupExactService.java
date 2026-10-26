package com.edufox.schoolerp.examination.setup.setup_examsetupexact;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Setup/ExamSetupExact.jsx. */
@Service("page_examination_setup_setup_examsetupexact_examsetupexactservice_java")
public class ExamSetupExactService extends PageArtifactService<ExamSetupExactRepository> {
    public ExamSetupExactService(ExamSetupExactRepository repository) {
        super(repository);
    }
}