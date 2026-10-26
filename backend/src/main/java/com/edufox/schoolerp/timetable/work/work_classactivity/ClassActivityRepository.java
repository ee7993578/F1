package com.edufox.schoolerp.timetable.work.work_classactivity;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for TimeTable/Work/ClassActivity.jsx. */
@Repository("page_com_edufox_schoolerp_timetable_work_work_classactivity_classactivityrepository_java")
public class ClassActivityRepository extends PageArtifactRepository {
    public ClassActivityRepository() {
        super("TimeTable", "work", "ClassActivity", "TimeTable/Work/ClassActivity.jsx");
    }
}