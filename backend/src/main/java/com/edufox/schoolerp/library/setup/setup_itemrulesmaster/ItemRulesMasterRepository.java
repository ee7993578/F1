package com.edufox.schoolerp.library.setup.setup_itemrulesmaster;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Library/Setup/ItemRulesMaster.jsx. */
@Repository("page_com_edufox_schoolerp_library_setup_setup_itemrulesmaster_itemrulesmasterrepository_java")
public class ItemRulesMasterRepository extends PageArtifactRepository {
    public ItemRulesMasterRepository() {
        super("Library", "setup", "ItemRulesMaster", "Library/Setup/ItemRulesMaster.jsx");
    }
}