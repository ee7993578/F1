package com.edufox.schoolerp.library.setup.setup_suppliercategory;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Library/Setup/SupplierCategory.jsx. */
@Repository("page_com_edufox_schoolerp_library_setup_setup_suppliercategory_suppliercategoryrepository_java")
public class SupplierCategoryRepository extends PageArtifactRepository {
    public SupplierCategoryRepository() {
        super("Library", "setup", "SupplierCategory", "Library/Setup/SupplierCategory.jsx");
    }
}