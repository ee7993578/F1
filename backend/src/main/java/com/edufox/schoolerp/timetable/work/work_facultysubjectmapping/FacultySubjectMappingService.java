package com.edufox.schoolerp.timetable.work.work_facultysubjectmapping;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for TimeTable/Work/FacultySubjectMapping.jsx. */
@Service("page_timetable_work_work_facultysubjectmapping_facultysubjectmappingservice_java")
public class FacultySubjectMappingService extends PageArtifactService<FacultySubjectMappingRepository> {
    public FacultySubjectMappingService(FacultySubjectMappingRepository repository) {
        super(repository);
    }
}