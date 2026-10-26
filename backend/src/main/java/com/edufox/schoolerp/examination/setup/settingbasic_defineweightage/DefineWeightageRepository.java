package com.edufox.schoolerp.examination.setup.settingbasic_defineweightage;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/SettingBasic/DefineWeightage.jsx. */
@Repository("page_com_edufox_schoolerp_examination_setup_settingbasic_defineweightage_defineweightagerepository_java")
public class DefineWeightageRepository extends PageArtifactRepository {
    public DefineWeightageRepository() {
        super("ExamMaster", "setup", "DefineWeightage", "ExamMaster/SettingBasic/DefineWeightage.jsx");
    }
}