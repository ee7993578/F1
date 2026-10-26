package com.edufox.schoolerp.examination.setup.settingbasic_definesubsubject;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/SettingBasic/DefineSubSubject.jsx. */
@Service("page_examination_setup_settingbasic_definesubsubject_definesubsubjectservice_java")
public class DefineSubSubjectService extends PageArtifactService<DefineSubSubjectRepository> {
    public DefineSubSubjectService(DefineSubSubjectRepository repository) {
        super(repository);
    }
}