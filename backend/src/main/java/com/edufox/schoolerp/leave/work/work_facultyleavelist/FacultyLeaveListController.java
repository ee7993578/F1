package com.edufox.schoolerp.leave.work.work_facultyleavelist;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Leave/Work/FacultyLeaveList.jsx. */
@RestController("page_leave_work_work_facultyleavelist_facultyleavelistcontroller_java")
@RequestMapping("/api/page-artifacts/leave/work/work_facultyleavelist")
public class FacultyLeaveListController extends PageArtifactController<FacultyLeaveListService> {
    public FacultyLeaveListController(FacultyLeaveListService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}