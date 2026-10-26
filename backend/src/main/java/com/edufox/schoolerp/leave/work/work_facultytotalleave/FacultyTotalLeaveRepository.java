package com.edufox.schoolerp.leave.work.work_facultytotalleave;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Leave/Work/FacultyTotalLeave.jsx. */
@Repository("page_com_edufox_schoolerp_leave_work_work_facultytotalleave_facultytotalleaverepository_java")
public class FacultyTotalLeaveRepository extends PageArtifactRepository {
    public FacultyTotalLeaveRepository() {
        super("Leave", "work", "FacultyTotalLeave", "Leave/Work/FacultyTotalLeave.jsx");
    }
}