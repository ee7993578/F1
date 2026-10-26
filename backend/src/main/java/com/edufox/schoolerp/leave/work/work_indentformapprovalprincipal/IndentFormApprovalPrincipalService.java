package com.edufox.schoolerp.leave.work.work_indentformapprovalprincipal;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Leave/Work/IndentFormApprovalPrincipal.jsx. */
@Service("page_leave_work_work_indentformapprovalprincipal_indentformapprovalprincipalservice_java")
public class IndentFormApprovalPrincipalService extends PageArtifactService<IndentFormApprovalPrincipalRepository> {
    public IndentFormApprovalPrincipalService(IndentFormApprovalPrincipalRepository repository) {
        super(repository);
    }
}