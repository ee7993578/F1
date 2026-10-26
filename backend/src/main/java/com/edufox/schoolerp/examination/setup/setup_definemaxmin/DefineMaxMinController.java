package com.edufox.schoolerp.examination.setup.setup_definemaxmin;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Setup/DefineMaxMin.jsx. */
@RestController("page_examination_setup_setup_definemaxmin_definemaxmincontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/setup/setup_definemaxmin")
public class DefineMaxMinController extends PageArtifactController<DefineMaxMinService> {
    public DefineMaxMinController(DefineMaxMinService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}