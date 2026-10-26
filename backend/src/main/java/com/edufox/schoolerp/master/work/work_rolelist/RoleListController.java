package com.edufox.schoolerp.master.work.work_rolelist;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for MasterSetting/Work/RoleList.jsx. */
@RestController("page_master_work_work_rolelist_rolelistcontroller_java")
@RequestMapping("/api/page-artifacts/mastersetting/work/work_rolelist")
public class RoleListController extends PageArtifactController<RoleListService> {
    public RoleListController(RoleListService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}