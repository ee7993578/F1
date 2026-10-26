package com.edufox.schoolerp.master.work.work_change_section_name;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for MasterSetting/Work/Change_Section_Name.jsx. */
@Service("page_master_work_work_change_section_name_change_section_nameservice_java")
public class Change_Section_NameService extends PageArtifactService<Change_Section_NameRepository> {
    public Change_Section_NameService(Change_Section_NameRepository repository) {
        super(repository);
    }
}