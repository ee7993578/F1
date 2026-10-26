package com.edufox.schoolerp.library.setup.setup_itemlanguage;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Library/Setup/ItemLanguage.jsx. */
@Repository("page_com_edufox_schoolerp_library_setup_setup_itemlanguage_itemlanguagerepository_java")
public class ItemLanguageRepository extends PageArtifactRepository {
    public ItemLanguageRepository() {
        super("Library", "setup", "ItemLanguage", "Library/Setup/ItemLanguage.jsx");
    }
}