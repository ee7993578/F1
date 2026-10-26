package com.edufox.schoolerp.examination.setup.setup_definemaxminexam;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Setup/DefineMaxMinExam.jsx. */
@Service("page_examination_setup_setup_definemaxminexam_definemaxminexamservice_java")
public class DefineMaxMinExamService extends PageArtifactService<DefineMaxMinExamRepository> {
    public DefineMaxMinExamService(DefineMaxMinExamRepository repository) {
        super(repository);
    }
}