package com.edufox.schoolerp.library.work.work_itementrymaster;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Library/Work/ItemEntryMaster.jsx. */
@RestController("page_library_work_work_itementrymaster_itementrymastercontroller_java")
@RequestMapping("/api/page-artifacts/library/work/work_itementrymaster")
public class ItemEntryMasterController extends PageArtifactController<ItemEntryMasterService> {
    public ItemEntryMasterController(ItemEntryMasterService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}