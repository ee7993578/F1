package com.edufox.schoolerp.library.setup.setup_itemlanguage;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Library/Setup/ItemLanguage.jsx. */
@Service("page_library_setup_setup_itemlanguage_itemlanguageservice_java")
public class ItemLanguageService extends PageArtifactService<ItemLanguageRepository> {
    public ItemLanguageService(ItemLanguageRepository repository) {
        super(repository);
    }
}