package com.edufox.schoolerp.leave.work.work_leaveallocation;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Leave/Work/LeaveAllocation.jsx. */
@Repository("page_com_edufox_schoolerp_leave_work_work_leaveallocation_leaveallocationrepository_java")
public class LeaveAllocationRepository extends PageArtifactRepository {
    public LeaveAllocationRepository() {
        super("Leave", "work", "LeaveAllocation", "Leave/Work/LeaveAllocation.jsx");
    }
}