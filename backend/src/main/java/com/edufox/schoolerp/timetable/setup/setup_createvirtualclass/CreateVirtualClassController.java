package com.edufox.schoolerp.timetable.setup.setup_createvirtualclass;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for TimeTable/Setup/CreateVirtualClass.jsx. */
@RestController("page_timetable_setup_setup_createvirtualclass_createvirtualclasscontroller_java")
@RequestMapping("/api/page-artifacts/timetable/setup/setup_createvirtualclass")
public class CreateVirtualClassController extends PageArtifactController<CreateVirtualClassService> {
    public CreateVirtualClassController(CreateVirtualClassService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}