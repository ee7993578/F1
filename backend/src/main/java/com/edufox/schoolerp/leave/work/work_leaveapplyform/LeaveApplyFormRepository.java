package com.edufox.schoolerp.leave.work.work_leaveapplyform;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Leave/Work/LeaveApplyForm.jsx. */
@Repository("page_com_edufox_schoolerp_leave_work_work_leaveapplyform_leaveapplyformrepository_java")
public class LeaveApplyFormRepository extends PageArtifactRepository {
    public LeaveApplyFormRepository() {
        super("Leave", "work", "LeaveApplyForm", "Leave/Work/LeaveApplyForm.jsx");
    }
}