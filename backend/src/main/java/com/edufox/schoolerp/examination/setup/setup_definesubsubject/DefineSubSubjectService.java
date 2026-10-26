package com.edufox.schoolerp.examination.setup.setup_definesubsubject;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Setup/DefineSubSubject.jsx. */
@Service("page_examination_setup_setup_definesubsubject_definesubsubjectservice_java")
public class DefineSubSubjectService extends PageArtifactService<DefineSubSubjectRepository> {
    public DefineSubSubjectService(DefineSubSubjectRepository repository) {
        super(repository);
    }
}