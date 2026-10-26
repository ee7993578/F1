package com.edufox.schoolerp.leave.work.work_leaveapprovalbyprincipal;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Leave/Work/LeaveApprovalByPrincipal.jsx. */
@Repository("page_com_edufox_schoolerp_leave_work_work_leaveapprovalbyprincipal_leaveapprovalbyprincipalrepository_java")
public class LeaveApprovalByPrincipalRepository extends PageArtifactRepository {
    public LeaveApprovalByPrincipalRepository() {
        super("Leave", "work", "LeaveApprovalByPrincipal", "Leave/Work/LeaveApprovalByPrincipal.jsx");
    }
}