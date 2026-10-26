package com.edufox.schoolerp.library.setup.setup_itemcategory;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Library/Setup/ItemCategory.jsx. */
@Repository("page_com_edufox_schoolerp_library_setup_setup_itemcategory_itemcategoryrepository_java")
public class ItemCategoryRepository extends PageArtifactRepository {
    public ItemCategoryRepository() {
        super("Library", "setup", "ItemCategory", "Library/Setup/ItemCategory.jsx");
    }
}