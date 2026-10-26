package com.edufox.schoolerp.examination.setup.settingprimary_defineattrib;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/SettingPrimary/DefineAttrib.jsx. */
@Repository("page_com_edufox_schoolerp_examination_setup_settingprimary_defineattrib_defineattribrepository_java")
public class DefineAttribRepository extends PageArtifactRepository {
    public DefineAttribRepository() {
        super("ExamMaster", "setup", "DefineAttrib", "ExamMaster/SettingPrimary/DefineAttrib.jsx");
    }
}