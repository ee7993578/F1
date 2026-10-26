package com.edufox.schoolerp.library.setup.setup_itemcategory;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Library/Setup/ItemCategory.jsx. */
@RestController("page_library_setup_setup_itemcategory_itemcategorycontroller_java")
@RequestMapping("/api/page-artifacts/library/setup/setup_itemcategory")
public class ItemCategoryController extends PageArtifactController<ItemCategoryService> {
    public ItemCategoryController(ItemCategoryService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}