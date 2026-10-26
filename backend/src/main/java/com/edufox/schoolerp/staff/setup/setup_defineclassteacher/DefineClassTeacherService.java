package com.edufox.schoolerp.staff.setup.setup_defineclassteacher;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Staff/Setup/DefineClassTeacher.jsx. */
@Service("page_staff_setup_setup_defineclassteacher_defineclassteacherservice_java")
public class DefineClassTeacherService extends PageArtifactService<DefineClassTeacherRepository> {
    public DefineClassTeacherService(DefineClassTeacherRepository repository) {
        super(repository);
    }
}