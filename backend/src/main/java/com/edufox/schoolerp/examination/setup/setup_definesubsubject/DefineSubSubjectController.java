package com.edufox.schoolerp.examination.setup.setup_definesubsubject;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Setup/DefineSubSubject.jsx. */
@RestController("page_examination_setup_setup_definesubsubject_definesubsubjectcontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/setup/setup_definesubsubject")
public class DefineSubSubjectController extends PageArtifactController<DefineSubSubjectService> {
    public DefineSubSubjectController(DefineSubSubjectService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}