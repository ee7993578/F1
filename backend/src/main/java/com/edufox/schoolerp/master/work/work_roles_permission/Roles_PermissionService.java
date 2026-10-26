package com.edufox.schoolerp.master.work.work_roles_permission;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for MasterSetting/Work/Roles_Permission.jsx. */
@Service("page_master_work_work_roles_permission_roles_permissionservice_java")
public class Roles_PermissionService extends PageArtifactService<Roles_PermissionRepository> {
    public Roles_PermissionService(Roles_PermissionRepository repository) {
        super(repository);
    }
}