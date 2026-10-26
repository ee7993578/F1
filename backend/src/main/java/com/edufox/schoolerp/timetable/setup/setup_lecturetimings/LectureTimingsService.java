package com.edufox.schoolerp.timetable.setup.setup_lecturetimings;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for TimeTable/Setup/LectureTimings.jsx. */
@Service("page_timetable_setup_setup_lecturetimings_lecturetimingsservice_java")
public class LectureTimingsService extends PageArtifactService<LectureTimingsRepository> {
    public LectureTimingsService(LectureTimingsRepository repository) {
        super(repository);
    }
}