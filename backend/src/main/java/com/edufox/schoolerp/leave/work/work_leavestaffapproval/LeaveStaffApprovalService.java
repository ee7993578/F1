package com.edufox.schoolerp.leave.work.work_leavestaffapproval;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Leave/Work/LeaveStaffApproval.jsx. */
@Service("page_leave_work_work_leavestaffapproval_leavestaffapprovalservice_java")
public class LeaveStaffApprovalService extends PageArtifactService<LeaveStaffApprovalRepository> {
    public LeaveStaffApprovalService(LeaveStaffApprovalRepository repository) {
        super(repository);
    }
}