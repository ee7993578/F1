package com.edufox.schoolerp.leave.work.work_facultytotalleave;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Leave/Work/FacultyTotalLeave.jsx. */
@RestController("page_leave_work_work_facultytotalleave_facultytotalleavecontroller_java")
@RequestMapping("/api/page-artifacts/leave/work/work_facultytotalleave")
public class FacultyTotalLeaveController extends PageArtifactController<FacultyTotalLeaveService> {
    public FacultyTotalLeaveController(FacultyTotalLeaveService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}