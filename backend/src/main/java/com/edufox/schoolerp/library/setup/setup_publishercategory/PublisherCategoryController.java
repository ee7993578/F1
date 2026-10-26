package com.edufox.schoolerp.library.setup.setup_publishercategory;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Library/Setup/PublisherCategory.jsx. */
@RestController("page_library_setup_setup_publishercategory_publishercategorycontroller_java")
@RequestMapping("/api/page-artifacts/library/setup/setup_publishercategory")
public class PublisherCategoryController extends PageArtifactController<PublisherCategoryService> {
    public PublisherCategoryController(PublisherCategoryService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}