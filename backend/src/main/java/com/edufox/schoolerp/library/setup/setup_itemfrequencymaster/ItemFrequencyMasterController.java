package com.edufox.schoolerp.library.setup.setup_itemfrequencymaster;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Library/Setup/ItemFrequencyMaster.jsx. */
@RestController("page_library_setup_setup_itemfrequencymaster_itemfrequencymastercontroller_java")
@RequestMapping("/api/page-artifacts/library/setup/setup_itemfrequencymaster")
public class ItemFrequencyMasterController extends PageArtifactController<ItemFrequencyMasterService> {
    public ItemFrequencyMasterController(ItemFrequencyMasterService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}