package com.edufox.schoolerp.examination.work.marksentry_definemarks;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/marksEntry/DefineMarks.jsx. */
@Service("page_examination_work_marksentry_definemarks_definemarksservice_java")
public class DefineMarksService extends PageArtifactService<DefineMarksRepository> {
    public DefineMarksService(DefineMarksRepository repository) {
        super(repository);
    }
}