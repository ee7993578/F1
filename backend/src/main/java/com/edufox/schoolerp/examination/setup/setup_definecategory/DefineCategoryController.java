package com.edufox.schoolerp.examination.setup.setup_definecategory;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Setup/DefineCategory.jsx. */
@RestController("page_examination_setup_setup_definecategory_definecategorycontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/setup/setup_definecategory")
public class DefineCategoryController extends PageArtifactController<DefineCategoryService> {
    public DefineCategoryController(DefineCategoryService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}