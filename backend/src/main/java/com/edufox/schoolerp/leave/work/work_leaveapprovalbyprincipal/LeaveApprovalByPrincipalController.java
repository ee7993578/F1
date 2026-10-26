package com.edufox.schoolerp.leave.work.work_leaveapprovalbyprincipal;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Leave/Work/LeaveApprovalByPrincipal.jsx. */
@RestController("page_leave_work_work_leaveapprovalbyprincipal_leaveapprovalbyprincipalcontroller_java")
@RequestMapping("/api/page-artifacts/leave/work/work_leaveapprovalbyprincipal")
public class LeaveApprovalByPrincipalController extends PageArtifactController<LeaveApprovalByPrincipalService> {
    public LeaveApprovalByPrincipalController(LeaveApprovalByPrincipalService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}