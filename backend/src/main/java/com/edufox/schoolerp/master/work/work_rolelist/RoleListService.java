package com.edufox.schoolerp.master.work.work_rolelist;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for MasterSetting/Work/RoleList.jsx. */
@Service("page_master_work_work_rolelist_rolelistservice_java")
public class RoleListService extends PageArtifactService<RoleListRepository> {
    public RoleListService(RoleListRepository repository) {
        super(repository);
    }
}