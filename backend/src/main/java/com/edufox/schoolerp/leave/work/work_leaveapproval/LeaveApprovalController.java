package com.edufox.schoolerp.leave.work.work_leaveapproval;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Leave/Work/LeaveApproval.jsx. */
@RestController("page_leave_work_work_leaveapproval_leaveapprovalcontroller_java")
@RequestMapping("/api/page-artifacts/leave/work/work_leaveapproval")
public class LeaveApprovalController extends PageArtifactController<LeaveApprovalService> {
    public LeaveApprovalController(LeaveApprovalService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}