package com.edufox.schoolerp.leave.work.work_facultyleavelist;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Leave/Work/FacultyLeaveList.jsx. */
@Service("page_leave_work_work_facultyleavelist_facultyleavelistservice_java")
public class FacultyLeaveListService extends PageArtifactService<FacultyLeaveListRepository> {
    public FacultyLeaveListService(FacultyLeaveListRepository repository) {
        super(repository);
    }
}