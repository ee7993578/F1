package com.edufox.schoolerp.student.setup.setup_classrepresentative;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Setup/ClassRepresentative.jsx. */
@RestController("page_student_setup_setup_classrepresentative_classrepresentativecontroller_java")
@RequestMapping("/api/page-artifacts/student/setup/setup_classrepresentative")
public class ClassRepresentativeController extends PageArtifactController<ClassRepresentativeService> {
    public ClassRepresentativeController(ClassRepresentativeService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}