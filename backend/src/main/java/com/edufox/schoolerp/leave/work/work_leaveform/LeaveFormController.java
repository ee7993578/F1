package com.edufox.schoolerp.leave.work.work_leaveform;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Leave/Work/LeaveForm.jsx. */
@RestController("page_leave_work_work_leaveform_leaveformcontroller_java")
@RequestMapping("/api/page-artifacts/leave/work/work_leaveform")
public class LeaveFormController extends PageArtifactController<LeaveFormService> {
    public LeaveFormController(LeaveFormService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}