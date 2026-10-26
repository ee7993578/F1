package com.edufox.schoolerp.timetable.setup.setup_lecturetimings;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for TimeTable/Setup/LectureTimings.jsx. */
@RestController("page_timetable_setup_setup_lecturetimings_lecturetimingscontroller_java")
@RequestMapping("/api/page-artifacts/timetable/setup/setup_lecturetimings")
public class LectureTimingsController extends PageArtifactController<LectureTimingsService> {
    public LectureTimingsController(LectureTimingsService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}