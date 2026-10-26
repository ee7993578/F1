package com.edufox.schoolerp.leave.work.work_facultyleavelist;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Leave/Work/FacultyLeaveList.jsx. */
@Repository("page_com_edufox_schoolerp_leave_work_work_facultyleavelist_facultyleavelistrepository_java")
public class FacultyLeaveListRepository extends PageArtifactRepository {
    public FacultyLeaveListRepository() {
        super("Leave", "work", "FacultyLeaveList", "Leave/Work/FacultyLeaveList.jsx");
    }
}