package com.edufox.schoolerp.timetable.work.work_facultysubjectmapping;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for TimeTable/Work/FacultySubjectMapping.jsx. */
@Repository("page_com_edufox_schoolerp_timetable_work_work_facultysubjectmapping_facultysubjectmappingrepository_java")
public class FacultySubjectMappingRepository extends PageArtifactRepository {
    public FacultySubjectMappingRepository() {
        super("TimeTable", "work", "FacultySubjectMapping", "TimeTable/Work/FacultySubjectMapping.jsx");
    }
}