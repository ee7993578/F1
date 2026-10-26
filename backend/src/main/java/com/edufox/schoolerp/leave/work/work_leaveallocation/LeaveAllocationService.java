package com.edufox.schoolerp.leave.work.work_leaveallocation;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Leave/Work/LeaveAllocation.jsx. */
@Service("page_leave_work_work_leaveallocation_leaveallocationservice_java")
public class LeaveAllocationService extends PageArtifactService<LeaveAllocationRepository> {
    public LeaveAllocationService(LeaveAllocationRepository repository) {
        super(repository);
    }
}