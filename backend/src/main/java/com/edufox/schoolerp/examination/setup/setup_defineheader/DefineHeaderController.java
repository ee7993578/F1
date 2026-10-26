package com.edufox.schoolerp.examination.setup.setup_defineheader;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Setup/DefineHeader.jsx. */
@RestController("page_examination_setup_setup_defineheader_defineheadercontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/setup/setup_defineheader")
public class DefineHeaderController extends PageArtifactController<DefineHeaderService> {
    public DefineHeaderController(DefineHeaderService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}