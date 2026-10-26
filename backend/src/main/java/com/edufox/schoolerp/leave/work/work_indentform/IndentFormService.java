package com.edufox.schoolerp.leave.work.work_indentform;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Leave/Work/IndentForm.jsx. */
@Service("page_leave_work_work_indentform_indentformservice_java")
public class IndentFormService extends PageArtifactService<IndentFormRepository> {
    public IndentFormService(IndentFormRepository repository) {
        super(repository);
    }
}