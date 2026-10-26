package com.edufox.schoolerp.library.work.work_supplierentry;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Library/Work/SupplierEntry.jsx. */
@RestController("page_library_work_work_supplierentry_supplierentrycontroller_java")
@RequestMapping("/api/page-artifacts/library/work/work_supplierentry")
public class SupplierEntryController extends PageArtifactController<SupplierEntryService> {
    public SupplierEntryController(SupplierEntryService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}