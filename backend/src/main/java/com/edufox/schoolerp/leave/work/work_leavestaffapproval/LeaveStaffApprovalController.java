package com.edufox.schoolerp.leave.work.work_leavestaffapproval;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Leave/Work/LeaveStaffApproval.jsx. */
@RestController("page_leave_work_work_leavestaffapproval_leavestaffapprovalcontroller_java")
@RequestMapping("/api/page-artifacts/leave/work/work_leavestaffapproval")
public class LeaveStaffApprovalController extends PageArtifactController<LeaveStaffApprovalService> {
    public LeaveStaffApprovalController(LeaveStaffApprovalService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}