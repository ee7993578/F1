package com.edufox.schoolerp.master.work.work_classsectionmaster;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for MasterSetting/Work/ClassSectionMaster.jsx. */
@Repository("page_com_edufox_schoolerp_master_work_work_classsectionmaster_classsectionmasterrepository_java")
public class ClassSectionMasterRepository extends PageArtifactRepository {
    public ClassSectionMasterRepository() {
        super("MasterSetting", "work", "ClassSectionMaster", "MasterSetting/Work/ClassSectionMaster.jsx");
    }
}