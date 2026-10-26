package com.edufox.schoolerp.library.setup.setup_suppliercategory;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Library/Setup/SupplierCategory.jsx. */
@RestController("page_library_setup_setup_suppliercategory_suppliercategorycontroller_java")
@RequestMapping("/api/page-artifacts/library/setup/setup_suppliercategory")
public class SupplierCategoryController extends PageArtifactController<SupplierCategoryService> {
    public SupplierCategoryController(SupplierCategoryService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}