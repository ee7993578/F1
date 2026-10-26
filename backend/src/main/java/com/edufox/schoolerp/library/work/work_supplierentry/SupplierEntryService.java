package com.edufox.schoolerp.library.work.work_supplierentry;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Library/Work/SupplierEntry.jsx. */
@Service("page_library_work_work_supplierentry_supplierentryservice_java")
public class SupplierEntryService extends PageArtifactService<SupplierEntryRepository> {
    public SupplierEntryService(SupplierEntryRepository repository) {
        super(repository);
    }
}