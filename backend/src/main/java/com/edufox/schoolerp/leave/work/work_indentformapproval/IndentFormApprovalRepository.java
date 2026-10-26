package com.edufox.schoolerp.leave.work.work_indentformapproval;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Leave/Work/IndentFormApproval.jsx. */
@Repository("page_com_edufox_schoolerp_leave_work_work_indentformapproval_indentformapprovalrepository_java")
public class IndentFormApprovalRepository extends PageArtifactRepository {
    public IndentFormApprovalRepository() {
        super("Leave", "work", "IndentFormApproval", "Leave/Work/IndentFormApproval.jsx");
    }
}