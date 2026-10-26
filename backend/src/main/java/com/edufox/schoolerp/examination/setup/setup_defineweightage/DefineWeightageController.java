package com.edufox.schoolerp.examination.setup.setup_defineweightage;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Setup/DefineWeightage.jsx. */
@RestController("page_examination_setup_setup_defineweightage_defineweightagecontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/setup/setup_defineweightage")
public class DefineWeightageController extends PageArtifactController<DefineWeightageService> {
    public DefineWeightageController(DefineWeightageService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}