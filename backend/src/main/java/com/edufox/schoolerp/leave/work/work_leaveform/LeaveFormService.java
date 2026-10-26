package com.edufox.schoolerp.leave.work.work_leaveform;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Leave/Work/LeaveForm.jsx. */
@Service("page_leave_work_work_leaveform_leaveformservice_java")
public class LeaveFormService extends PageArtifactService<LeaveFormRepository> {
    public LeaveFormService(LeaveFormRepository repository) {
        super(repository);
    }
}