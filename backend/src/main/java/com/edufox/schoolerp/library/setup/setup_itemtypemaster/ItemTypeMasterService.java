package com.edufox.schoolerp.library.setup.setup_itemtypemaster;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Library/Setup/ItemTypeMaster.jsx. */
@Service("page_library_setup_setup_itemtypemaster_itemtypemasterservice_java")
public class ItemTypeMasterService extends PageArtifactService<ItemTypeMasterRepository> {
    public ItemTypeMasterService(ItemTypeMasterRepository repository) {
        super(repository);
    }
}