package com.edufox.schoolerp.examination.setup.setup_subjectmaster;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Setup/SubjectMaster.jsx. */
@Service("page_examination_setup_setup_subjectmaster_subjectmasterservice_java")
public class SubjectMasterService extends PageArtifactService<SubjectMasterRepository> {
    public SubjectMasterService(SubjectMasterRepository repository) {
        super(repository);
    }
}