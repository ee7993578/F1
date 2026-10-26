package com.edufox.schoolerp.timetable.work.work_alttimetable;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for TimeTable/Work/AltTimetable.jsx. */
@Repository("page_com_edufox_schoolerp_timetable_work_work_alttimetable_alttimetablerepository_java")
public class AltTimetableRepository extends PageArtifactRepository {
    public AltTimetableRepository() {
        super("TimeTable", "work", "AltTimetable", "TimeTable/Work/AltTimetable.jsx");
    }
}