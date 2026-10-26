package com.edufox.schoolerp.library.setup.setup_itemrulesmaster;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Library/Setup/ItemRulesMaster.jsx. */
@RestController("page_library_setup_setup_itemrulesmaster_itemrulesmastercontroller_java")
@RequestMapping("/api/page-artifacts/library/setup/setup_itemrulesmaster")
public class ItemRulesMasterController extends PageArtifactController<ItemRulesMasterService> {
    public ItemRulesMasterController(ItemRulesMasterService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}