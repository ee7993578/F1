package com.edufox.schoolerp.examination.setup.settingbasic_defineprsubject;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/SettingBasic/DefinePrSubject.jsx. */
@Service("page_examination_setup_settingbasic_defineprsubject_defineprsubjectservice_java")
public class DefinePrSubjectService extends PageArtifactService<DefinePrSubjectRepository> {
    public DefinePrSubjectService(DefinePrSubjectRepository repository) {
        super(repository);
    }
}