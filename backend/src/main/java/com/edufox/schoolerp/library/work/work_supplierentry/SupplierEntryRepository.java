package com.edufox.schoolerp.library.work.work_supplierentry;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Library/Work/SupplierEntry.jsx. */
@Repository("page_com_edufox_schoolerp_library_work_work_supplierentry_supplierentryrepository_java")
public class SupplierEntryRepository extends PageArtifactRepository {
    public SupplierEntryRepository() {
        super("Library", "work", "SupplierEntry", "Library/Work/SupplierEntry.jsx");
    }
}