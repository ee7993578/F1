package com.edufox.schoolerp.timetable.work.work_assigntimetable;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for TimeTable/Work/AssignTimeTable.jsx. */
@RestController("page_timetable_work_work_assigntimetable_assigntimetablecontroller_java")
@RequestMapping("/api/page-artifacts/timetable/work/work_assigntimetable")
public class AssignTimeTableController extends PageArtifactController<AssignTimeTableService> {
    public AssignTimeTableController(AssignTimeTableService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}