package com.edufox.schoolerp.master.work.work_change_section_name;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for MasterSetting/Work/Change_Section_Name.jsx. */
@Repository("page_com_edufox_schoolerp_master_work_work_change_section_name_change_section_namerepository_java")
public class Change_Section_NameRepository extends PageArtifactRepository {
    public Change_Section_NameRepository() {
        super("MasterSetting", "work", "Change_Section_Name", "MasterSetting/Work/Change_Section_Name.jsx");
    }
}