package com.edufox.schoolerp.library.setup.setup_suppliercategory;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Library/Setup/SupplierCategory.jsx. */
@Service("page_library_setup_setup_suppliercategory_suppliercategoryservice_java")
public class SupplierCategoryService extends PageArtifactService<SupplierCategoryRepository> {
    public SupplierCategoryService(SupplierCategoryRepository repository) {
        super(repository);
    }
}