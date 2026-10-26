package com.edufox.schoolerp.examination.setup.settingbasic_subjectmaster;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/SettingBasic/SubjectMaster.jsx. */
@Service("page_examination_setup_settingbasic_subjectmaster_subjectmasterservice_java")
public class SubjectMasterService extends PageArtifactService<SubjectMasterRepository> {
    public SubjectMasterService(SubjectMasterRepository repository) {
        super(repository);
    }
}