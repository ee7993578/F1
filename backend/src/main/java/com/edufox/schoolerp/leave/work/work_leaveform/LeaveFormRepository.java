package com.edufox.schoolerp.leave.work.work_leaveform;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Leave/Work/LeaveForm.jsx. */
@Repository("page_com_edufox_schoolerp_leave_work_work_leaveform_leaveformrepository_java")
public class LeaveFormRepository extends PageArtifactRepository {
    public LeaveFormRepository() {
        super("Leave", "work", "LeaveForm", "Leave/Work/LeaveForm.jsx");
    }
}