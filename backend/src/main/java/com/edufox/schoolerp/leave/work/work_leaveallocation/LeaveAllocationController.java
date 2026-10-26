package com.edufox.schoolerp.leave.work.work_leaveallocation;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Leave/Work/LeaveAllocation.jsx. */
@RestController("page_leave_work_work_leaveallocation_leaveallocationcontroller_java")
@RequestMapping("/api/page-artifacts/leave/work/work_leaveallocation")
public class LeaveAllocationController extends PageArtifactController<LeaveAllocationService> {
    public LeaveAllocationController(LeaveAllocationService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}