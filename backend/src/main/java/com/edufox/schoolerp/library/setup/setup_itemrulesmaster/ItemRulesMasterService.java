package com.edufox.schoolerp.library.setup.setup_itemrulesmaster;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Library/Setup/ItemRulesMaster.jsx. */
@Service("page_library_setup_setup_itemrulesmaster_itemrulesmasterservice_java")
public class ItemRulesMasterService extends PageArtifactService<ItemRulesMasterRepository> {
    public ItemRulesMasterService(ItemRulesMasterRepository repository) {
        super(repository);
    }
}