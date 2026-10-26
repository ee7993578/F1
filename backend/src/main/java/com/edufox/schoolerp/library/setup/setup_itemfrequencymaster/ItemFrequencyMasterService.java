package com.edufox.schoolerp.library.setup.setup_itemfrequencymaster;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Library/Setup/ItemFrequencyMaster.jsx. */
@Service("page_library_setup_setup_itemfrequencymaster_itemfrequencymasterservice_java")
public class ItemFrequencyMasterService extends PageArtifactService<ItemFrequencyMasterRepository> {
    public ItemFrequencyMasterService(ItemFrequencyMasterRepository repository) {
        super(repository);
    }
}