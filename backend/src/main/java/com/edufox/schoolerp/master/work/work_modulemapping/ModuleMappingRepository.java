package com.edufox.schoolerp.master.work.work_modulemapping;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for MasterSetting/Work/ModuleMapping.jsx. */
@Repository("page_com_edufox_schoolerp_master_work_work_modulemapping_modulemappingrepository_java")
public class ModuleMappingRepository extends PageArtifactRepository {
    public ModuleMappingRepository() {
        super("MasterSetting", "work", "ModuleMapping", "MasterSetting/Work/ModuleMapping.jsx");
    }
}