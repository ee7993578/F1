package com.edufox.schoolerp.examination.setup.settingbasic_define_classsubject;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/SettingBasic/Define_ClassSubject.jsx. */
@Service("page_examination_setup_settingbasic_define_classsubject_define_classsubjectservice_java")
public class Define_ClassSubjectService extends PageArtifactService<Define_ClassSubjectRepository> {
    public Define_ClassSubjectService(Define_ClassSubjectRepository repository) {
        super(repository);
    }
}