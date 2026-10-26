package com.edufox.schoolerp.timetable.work.work_classactivity;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for TimeTable/Work/ClassActivity.jsx. */
@RestController("page_timetable_work_work_classactivity_classactivitycontroller_java")
@RequestMapping("/api/page-artifacts/timetable/work/work_classactivity")
public class ClassActivityController extends PageArtifactController<ClassActivityService> {
    public ClassActivityController(ClassActivityService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}