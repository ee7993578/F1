package com.edufox.schoolerp.master.work.work_classsectionmaster;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for MasterSetting/Work/ClassSectionMaster.jsx. */
@Service("page_master_work_work_classsectionmaster_classsectionmasterservice_java")
public class ClassSectionMasterService extends PageArtifactService<ClassSectionMasterRepository> {
    public ClassSectionMasterService(ClassSectionMasterRepository repository) {
        super(repository);
    }
}