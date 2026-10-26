package com.edufox.schoolerp.master.work.work_rolelist;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for MasterSetting/Work/RoleList.jsx. */
@Repository("page_com_edufox_schoolerp_master_work_work_rolelist_rolelistrepository_java")
public class RoleListRepository extends PageArtifactRepository {
    public RoleListRepository() {
        super("MasterSetting", "work", "RoleList", "MasterSetting/Work/RoleList.jsx");
    }
}