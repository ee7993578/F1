package com.edufox.schoolerp.timetable.setup.setup_createvirtualclass;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for TimeTable/Setup/CreateVirtualClass.jsx. */
@Repository("page_com_edufox_schoolerp_timetable_setup_setup_createvirtualclass_createvirtualclassrepository_java")
public class CreateVirtualClassRepository extends PageArtifactRepository {
    public CreateVirtualClassRepository() {
        super("TimeTable", "setup", "CreateVirtualClass", "TimeTable/Setup/CreateVirtualClass.jsx");
    }
}