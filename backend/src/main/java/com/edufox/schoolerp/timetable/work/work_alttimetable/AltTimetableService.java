package com.edufox.schoolerp.timetable.work.work_alttimetable;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for TimeTable/Work/AltTimetable.jsx. */
@Service("page_timetable_work_work_alttimetable_alttimetableservice_java")
public class AltTimetableService extends PageArtifactService<AltTimetableRepository> {
    public AltTimetableService(AltTimetableRepository repository) {
        super(repository);
    }
}