package com.edufox.schoolerp.library.setup.setup_itemtypemaster;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Library/Setup/ItemTypeMaster.jsx. */
@Repository("page_com_edufox_schoolerp_library_setup_setup_itemtypemaster_itemtypemasterrepository_java")
public class ItemTypeMasterRepository extends PageArtifactRepository {
    public ItemTypeMasterRepository() {
        super("Library", "setup", "ItemTypeMaster", "Library/Setup/ItemTypeMaster.jsx");
    }
}