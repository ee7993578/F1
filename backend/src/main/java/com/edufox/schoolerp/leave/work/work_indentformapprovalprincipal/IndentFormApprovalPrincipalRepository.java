package com.edufox.schoolerp.leave.work.work_indentformapprovalprincipal;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Leave/Work/IndentFormApprovalPrincipal.jsx. */
@Repository("page_com_edufox_schoolerp_leave_work_work_indentformapprovalprincipal_indentformapprovalprincipalrepository_java")
public class IndentFormApprovalPrincipalRepository extends PageArtifactRepository {
    public IndentFormApprovalPrincipalRepository() {
        super("Leave", "work", "IndentFormApprovalPrincipal", "Leave/Work/IndentFormApprovalPrincipal.jsx");
    }
}