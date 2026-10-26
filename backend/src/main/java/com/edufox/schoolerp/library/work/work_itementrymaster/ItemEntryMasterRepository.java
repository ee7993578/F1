package com.edufox.schoolerp.library.work.work_itementrymaster;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Library/Work/ItemEntryMaster.jsx. */
@Repository("page_com_edufox_schoolerp_library_work_work_itementrymaster_itementrymasterrepository_java")
public class ItemEntryMasterRepository extends PageArtifactRepository {
    public ItemEntryMasterRepository() {
        super("Library", "work", "ItemEntryMaster", "Library/Work/ItemEntryMaster.jsx");
    }
}