package com.edufox.schoolerp.examination.setup.settingprimary_defineheader;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/SettingPrimary/DefineHeader.jsx. */
@Repository("page_com_edufox_schoolerp_examination_setup_settingprimary_defineheader_defineheaderrepository_java")
public class DefineHeaderRepository extends PageArtifactRepository {
    public DefineHeaderRepository() {
        super("ExamMaster", "setup", "DefineHeader", "ExamMaster/SettingPrimary/DefineHeader.jsx");
    }
}