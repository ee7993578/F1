package com.edufox.schoolerp.timetable.setup.setup_createvirtualclass;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for TimeTable/Setup/CreateVirtualClass.jsx. */
@Service("page_timetable_setup_setup_createvirtualclass_createvirtualclassservice_java")
public class CreateVirtualClassService extends PageArtifactService<CreateVirtualClassRepository> {
    public CreateVirtualClassService(CreateVirtualClassRepository repository) {
        super(repository);
    }
}