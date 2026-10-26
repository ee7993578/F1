package com.edufox.schoolerp.examination.setup.settingbasic_definesubsubject;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/SettingBasic/DefineSubSubject.jsx. */
@Repository("page_com_edufox_schoolerp_examination_setup_settingbasic_definesubsubject_definesubsubjectrepository_java")
public class DefineSubSubjectRepository extends PageArtifactRepository {
    public DefineSubSubjectRepository() {
        super("ExamMaster", "setup", "DefineSubSubject", "ExamMaster/SettingBasic/DefineSubSubject.jsx");
    }
}