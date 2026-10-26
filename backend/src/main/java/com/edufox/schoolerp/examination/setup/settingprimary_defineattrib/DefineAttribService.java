package com.edufox.schoolerp.examination.setup.settingprimary_defineattrib;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/SettingPrimary/DefineAttrib.jsx. */
@Service("page_examination_setup_settingprimary_defineattrib_defineattribservice_java")
public class DefineAttribService extends PageArtifactService<DefineAttribRepository> {
    public DefineAttribService(DefineAttribRepository repository) {
        super(repository);
    }
}