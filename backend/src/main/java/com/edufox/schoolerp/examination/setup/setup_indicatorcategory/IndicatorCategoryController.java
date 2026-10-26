package com.edufox.schoolerp.examination.setup.setup_indicatorcategory;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Setup/IndicatorCategory.jsx. */
@RestController("page_examination_setup_setup_indicatorcategory_indicatorcategorycontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/setup/setup_indicatorcategory")
public class IndicatorCategoryController extends PageArtifactController<IndicatorCategoryService> {
    public IndicatorCategoryController(IndicatorCategoryService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}