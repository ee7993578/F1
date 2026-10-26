package com.edufox.schoolerp.leave.work.work_leaveapprovalbyprincipal;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Leave/Work/LeaveApprovalByPrincipal.jsx. */
@Service("page_leave_work_work_leaveapprovalbyprincipal_leaveapprovalbyprincipalservice_java")
public class LeaveApprovalByPrincipalService extends PageArtifactService<LeaveApprovalByPrincipalRepository> {
    public LeaveApprovalByPrincipalService(LeaveApprovalByPrincipalRepository repository) {
        super(repository);
    }
}