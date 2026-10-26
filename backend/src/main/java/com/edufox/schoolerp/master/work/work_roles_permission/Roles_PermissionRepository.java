package com.edufox.schoolerp.master.work.work_roles_permission;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for MasterSetting/Work/Roles_Permission.jsx. */
@Repository("page_com_edufox_schoolerp_master_work_work_roles_permission_roles_permissionrepository_java")
public class Roles_PermissionRepository extends PageArtifactRepository {
    public Roles_PermissionRepository() {
        super("MasterSetting", "work", "Roles_Permission", "MasterSetting/Work/Roles_Permission.jsx");
    }
}