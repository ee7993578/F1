package com.edufox.schoolerp.leave.work.work_indentformapproval;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Leave/Work/IndentFormApproval.jsx. */
@Service("page_leave_work_work_indentformapproval_indentformapprovalservice_java")
public class IndentFormApprovalService extends PageArtifactService<IndentFormApprovalRepository> {
    public IndentFormApprovalService(IndentFormApprovalRepository repository) {
        super(repository);
    }
}