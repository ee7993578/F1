package com.edufox.schoolerp.examination.setup.settingbasic_definemaxmin;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/SettingBasic/DefineMaxMin.jsx. */
@Repository("page_com_edufox_schoolerp_examination_setup_settingbasic_definemaxmin_definemaxminrepository_java")
public class DefineMaxMinRepository extends PageArtifactRepository {
    public DefineMaxMinRepository() {
        super("ExamMaster", "setup", "DefineMaxMin", "ExamMaster/SettingBasic/DefineMaxMin.jsx");
    }
}