package com.edufox.schoolerp.examination.setup.settingbasic_defineweightage;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/SettingBasic/DefineWeightage.jsx. */
@Service("page_examination_setup_settingbasic_defineweightage_defineweightageservice_java")
public class DefineWeightageService extends PageArtifactService<DefineWeightageRepository> {
    public DefineWeightageService(DefineWeightageRepository repository) {
        super(repository);
    }
}