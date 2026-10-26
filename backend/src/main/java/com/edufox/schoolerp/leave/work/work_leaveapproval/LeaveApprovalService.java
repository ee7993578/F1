package com.edufox.schoolerp.leave.work.work_leaveapproval;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Leave/Work/LeaveApproval.jsx. */
@Service("page_leave_work_work_leaveapproval_leaveapprovalservice_java")
public class LeaveApprovalService extends PageArtifactService<LeaveApprovalRepository> {
    public LeaveApprovalService(LeaveApprovalRepository repository) {
        super(repository);
    }
}