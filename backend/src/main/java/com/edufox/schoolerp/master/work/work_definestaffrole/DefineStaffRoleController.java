package com.edufox.schoolerp.master.work.work_definestaffrole;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for MasterSetting/Work/DefineStaffRole.jsx. */
@RestController("page_master_work_work_definestaffrole_definestaffrolecontroller_java")
@RequestMapping("/api/page-artifacts/mastersetting/work/work_definestaffrole")
public class DefineStaffRoleController extends PageArtifactController<DefineStaffRoleService> {
    public DefineStaffRoleController(DefineStaffRoleService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}