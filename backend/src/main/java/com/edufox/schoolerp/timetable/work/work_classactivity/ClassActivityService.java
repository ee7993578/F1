package com.edufox.schoolerp.timetable.work.work_classactivity;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for TimeTable/Work/ClassActivity.jsx. */
@Service("page_timetable_work_work_classactivity_classactivityservice_java")
public class ClassActivityService extends PageArtifactService<ClassActivityRepository> {
    public ClassActivityService(ClassActivityRepository repository) {
        super(repository);
    }
}