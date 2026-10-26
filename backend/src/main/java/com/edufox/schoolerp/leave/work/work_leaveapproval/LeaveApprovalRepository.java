package com.edufox.schoolerp.leave.work.work_leaveapproval;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Leave/Work/LeaveApproval.jsx. */
@Repository("page_com_edufox_schoolerp_leave_work_work_leaveapproval_leaveapprovalrepository_java")
public class LeaveApprovalRepository extends PageArtifactRepository {
    public LeaveApprovalRepository() {
        super("Leave", "work", "LeaveApproval", "Leave/Work/LeaveApproval.jsx");
    }
}