package com.edufox.schoolerp.master.work.work_modulemapping;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for MasterSetting/Work/ModuleMapping.jsx. */
@Service("page_master_work_work_modulemapping_modulemappingservice_java")
public class ModuleMappingService extends PageArtifactService<ModuleMappingRepository> {
    public ModuleMappingService(ModuleMappingRepository repository) {
        super(repository);
    }
}