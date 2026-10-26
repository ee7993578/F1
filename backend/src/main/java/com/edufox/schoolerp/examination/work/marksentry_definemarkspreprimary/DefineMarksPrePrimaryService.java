package com.edufox.schoolerp.examination.work.marksentry_definemarkspreprimary;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/marksEntry/DefineMarksPrePrimary.jsx. */
@Service("page_examination_work_marksentry_definemarkspreprimary_definemarkspreprimaryservice_java")
public class DefineMarksPrePrimaryService extends PageArtifactService<DefineMarksPrePrimaryRepository> {
    public DefineMarksPrePrimaryService(DefineMarksPrePrimaryRepository repository) {
        super(repository);
    }
}