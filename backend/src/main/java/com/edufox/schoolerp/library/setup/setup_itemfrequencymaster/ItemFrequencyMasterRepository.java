package com.edufox.schoolerp.library.setup.setup_itemfrequencymaster;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Library/Setup/ItemFrequencyMaster.jsx. */
@Repository("page_com_edufox_schoolerp_library_setup_setup_itemfrequencymaster_itemfrequencymasterrepository_java")
public class ItemFrequencyMasterRepository extends PageArtifactRepository {
    public ItemFrequencyMasterRepository() {
        super("Library", "setup", "ItemFrequencyMaster", "Library/Setup/ItemFrequencyMaster.jsx");
    }
}