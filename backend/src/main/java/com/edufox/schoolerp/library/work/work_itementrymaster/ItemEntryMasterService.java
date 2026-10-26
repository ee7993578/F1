package com.edufox.schoolerp.library.work.work_itementrymaster;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Library/Work/ItemEntryMaster.jsx. */
@Service("page_library_work_work_itementrymaster_itementrymasterservice_java")
public class ItemEntryMasterService extends PageArtifactService<ItemEntryMasterRepository> {
    public ItemEntryMasterService(ItemEntryMasterRepository repository) {
        super(repository);
    }
}