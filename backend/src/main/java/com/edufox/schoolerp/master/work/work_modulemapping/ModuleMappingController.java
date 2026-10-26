package com.edufox.schoolerp.master.work.work_modulemapping;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for MasterSetting/Work/ModuleMapping.jsx. */
@RestController("page_master_work_work_modulemapping_modulemappingcontroller_java")
@RequestMapping("/api/page-artifacts/mastersetting/work/work_modulemapping")
public class ModuleMappingController extends PageArtifactController<ModuleMappingService> {
    public ModuleMappingController(ModuleMappingService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}