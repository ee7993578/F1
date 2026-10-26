package com.edufox.schoolerp.examination.setup.settingprimary_defineheader;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/SettingPrimary/DefineHeader.jsx. */
@Service("page_examination_setup_settingprimary_defineheader_defineheaderservice_java")
public class DefineHeaderService extends PageArtifactService<DefineHeaderRepository> {
    public DefineHeaderService(DefineHeaderRepository repository) {
        super(repository);
    }
}