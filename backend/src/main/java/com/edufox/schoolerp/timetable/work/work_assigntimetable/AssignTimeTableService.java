package com.edufox.schoolerp.timetable.work.work_assigntimetable;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for TimeTable/Work/AssignTimeTable.jsx. */
@Service("page_timetable_work_work_assigntimetable_assigntimetableservice_java")
public class AssignTimeTableService extends PageArtifactService<AssignTimeTableRepository> {
    public AssignTimeTableService(AssignTimeTableRepository repository) {
        super(repository);
    }
}