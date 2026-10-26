package com.edufox.schoolerp.leave.work.work_leavestaffapproval;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Leave/Work/LeaveStaffApproval.jsx. */
@Repository("page_com_edufox_schoolerp_leave_work_work_leavestaffapproval_leavestaffapprovalrepository_java")
public class LeaveStaffApprovalRepository extends PageArtifactRepository {
    public LeaveStaffApprovalRepository() {
        super("Leave", "work", "LeaveStaffApproval", "Leave/Work/LeaveStaffApproval.jsx");
    }
}