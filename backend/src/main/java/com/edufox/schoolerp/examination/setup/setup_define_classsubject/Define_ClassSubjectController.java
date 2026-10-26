package com.edufox.schoolerp.examination.setup.setup_define_classsubject;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Setup/Define_ClassSubject.jsx. */
@RestController("page_examination_setup_setup_define_classsubject_define_classsubjectcontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/setup/setup_define_classsubject")
public class Define_ClassSubjectController extends PageArtifactController<Define_ClassSubjectService> {
    public Define_ClassSubjectController(Define_ClassSubjectService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}