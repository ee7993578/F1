package com.edufox.schoolerp.master.work.work_definestaffrole;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for MasterSetting/Work/DefineStaffRole.jsx. */
@Repository("page_com_edufox_schoolerp_master_work_work_definestaffrole_definestaffrolerepository_java")
public class DefineStaffRoleRepository extends PageArtifactRepository {
    public DefineStaffRoleRepository() {
        super("MasterSetting", "work", "DefineStaffRole", "MasterSetting/Work/DefineStaffRole.jsx");
    }
}