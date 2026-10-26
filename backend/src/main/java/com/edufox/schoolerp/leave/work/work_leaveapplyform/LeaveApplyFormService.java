package com.edufox.schoolerp.leave.work.work_leaveapplyform;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Leave/Work/LeaveApplyForm.jsx. */
@Service("page_leave_work_work_leaveapplyform_leaveapplyformservice_java")
public class LeaveApplyFormService extends PageArtifactService<LeaveApplyFormRepository> {
    public LeaveApplyFormService(LeaveApplyFormRepository repository) {
        super(repository);
    }
}