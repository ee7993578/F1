package com.edufox.schoolerp.timetable.work.work_alttimetable;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for TimeTable/Work/AltTimetable.jsx. */
@RestController("page_timetable_work_work_alttimetable_alttimetablecontroller_java")
@RequestMapping("/api/page-artifacts/timetable/work/work_alttimetable")
public class AltTimetableController extends PageArtifactController<AltTimetableService> {
    public AltTimetableController(AltTimetableService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}