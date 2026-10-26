package com.edufox.schoolerp.staff.setup.setup_defineclassteacher;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Staff/Setup/DefineClassTeacher.jsx. */
@RestController("page_staff_setup_setup_defineclassteacher_defineclassteachercontroller_java")
@RequestMapping("/api/page-artifacts/staff/setup/setup_defineclassteacher")
public class DefineClassTeacherController extends PageArtifactController<DefineClassTeacherService> {
    public DefineClassTeacherController(DefineClassTeacherService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}