package com.edufox.schoolerp.timetable.work.work_assigntimetable;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for TimeTable/Work/AssignTimeTable.jsx. */
@Repository("page_com_edufox_schoolerp_timetable_work_work_assigntimetable_assigntimetablerepository_java")
public class AssignTimeTableRepository extends PageArtifactRepository {
    public AssignTimeTableRepository() {
        super("TimeTable", "work", "AssignTimeTable", "TimeTable/Work/AssignTimeTable.jsx");
    }
}