package com.edufox.schoolerp.library.setup.setup_itemlanguage;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Library/Setup/ItemLanguage.jsx. */
@RestController("page_library_setup_setup_itemlanguage_itemlanguagecontroller_java")
@RequestMapping("/api/page-artifacts/library/setup/setup_itemlanguage")
public class ItemLanguageController extends PageArtifactController<ItemLanguageService> {
    public ItemLanguageController(ItemLanguageService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}