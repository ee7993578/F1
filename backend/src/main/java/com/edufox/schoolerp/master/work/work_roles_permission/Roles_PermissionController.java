package com.edufox.schoolerp.master.work.work_roles_permission;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for MasterSetting/Work/Roles_Permission.jsx. */
@RestController("page_master_work_work_roles_permission_roles_permissioncontroller_java")
@RequestMapping("/api/page-artifacts/mastersetting/work/work_roles_permission")
public class Roles_PermissionController extends PageArtifactController<Roles_PermissionService> {
    public Roles_PermissionController(Roles_PermissionService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}