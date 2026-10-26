package com.edufox.schoolerp.timetable.setup.setup_lecturetimings;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for TimeTable/Setup/LectureTimings.jsx. */
@Repository("page_com_edufox_schoolerp_timetable_setup_setup_lecturetimings_lecturetimingsrepository_java")
public class LectureTimingsRepository extends PageArtifactRepository {
    public LectureTimingsRepository() {
        super("TimeTable", "setup", "LectureTimings", "TimeTable/Setup/LectureTimings.jsx");
    }
}