package com.edufox.schoolerp.master.work.work_definestaffrole;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for MasterSetting/Work/DefineStaffRole.jsx. */
@Service("page_master_work_work_definestaffrole_definestaffroleservice_java")
public class DefineStaffRoleService extends PageArtifactService<DefineStaffRoleRepository> {
    public DefineStaffRoleService(DefineStaffRoleRepository repository) {
        super(repository);
    }
}