package com.edufox.schoolerp.staff.setup.setup_defineclassteacher;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Staff/Setup/DefineClassTeacher.jsx. */
@Repository("page_com_edufox_schoolerp_staff_setup_setup_defineclassteacher_defineclassteacherrepository_java")
public class DefineClassTeacherRepository extends PageArtifactRepository {
    public DefineClassTeacherRepository() {
        super("Staff", "setup", "DefineClassTeacher", "Staff/Setup/DefineClassTeacher.jsx");
    }
}