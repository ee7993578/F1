package com.edufox.schoolerp.examination.setup.setup_defineattrib;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Setup/DefineAttrib.jsx. */
@RestController("page_examination_setup_setup_defineattrib_defineattribcontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/setup/setup_defineattrib")
public class DefineAttribController extends PageArtifactController<DefineAttribService> {
    public DefineAttribController(DefineAttribService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}