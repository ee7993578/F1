package com.edufox.schoolerp.library.setup.setup_itemcategory;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Library/Setup/ItemCategory.jsx. */
@Service("page_library_setup_setup_itemcategory_itemcategoryservice_java")
public class ItemCategoryService extends PageArtifactService<ItemCategoryRepository> {
    public ItemCategoryService(ItemCategoryRepository repository) {
        super(repository);
    }
}