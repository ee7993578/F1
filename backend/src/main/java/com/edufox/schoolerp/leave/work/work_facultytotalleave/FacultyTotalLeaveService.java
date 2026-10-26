package com.edufox.schoolerp.leave.work.work_facultytotalleave;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Leave/Work/FacultyTotalLeave.jsx. */
@Service("page_leave_work_work_facultytotalleave_facultytotalleaveservice_java")
public class FacultyTotalLeaveService extends PageArtifactService<FacultyTotalLeaveRepository> {
    public FacultyTotalLeaveService(FacultyTotalLeaveRepository repository) {
        super(repository);
    }
}