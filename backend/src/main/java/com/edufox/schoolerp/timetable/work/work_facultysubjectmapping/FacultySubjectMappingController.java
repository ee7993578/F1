package com.edufox.schoolerp.timetable.work.work_facultysubjectmapping;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for TimeTable/Work/FacultySubjectMapping.jsx. */
@RestController("page_timetable_work_work_facultysubjectmapping_facultysubjectmappingcontroller_java")
@RequestMapping("/api/page-artifacts/timetable/work/work_facultysubjectmapping")
public class FacultySubjectMappingController extends PageArtifactController<FacultySubjectMappingService> {
    public FacultySubjectMappingController(FacultySubjectMappingService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}