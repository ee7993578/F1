package com.edufox.schoolerp.leave.work.work_leaveapplyform;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Leave/Work/LeaveApplyForm.jsx. */
@RestController("page_leave_work_work_leaveapplyform_leaveapplyformcontroller_java")
@RequestMapping("/api/page-artifacts/leave/work/work_leaveapplyform")
public class LeaveApplyFormController extends PageArtifactController<LeaveApplyFormService> {
    public LeaveApplyFormController(LeaveApplyFormService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}