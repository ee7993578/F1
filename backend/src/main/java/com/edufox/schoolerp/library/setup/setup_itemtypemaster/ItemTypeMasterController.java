package com.edufox.schoolerp.library.setup.setup_itemtypemaster;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Library/Setup/ItemTypeMaster.jsx. */
@RestController("page_library_setup_setup_itemtypemaster_itemtypemastercontroller_java")
@RequestMapping("/api/page-artifacts/library/setup/setup_itemtypemaster")
public class ItemTypeMasterController extends PageArtifactController<ItemTypeMasterService> {
    public ItemTypeMasterController(ItemTypeMasterService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}