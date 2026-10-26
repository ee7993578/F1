package com.edufox.schoolerp.examination.setup.settingbasic_defineprsubject;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/SettingBasic/DefinePrSubject.jsx. */
@Repository("page_com_edufox_schoolerp_examination_setup_settingbasic_defineprsubject_defineprsubjectrepository_java")
public class DefinePrSubjectRepository extends PageArtifactRepository {
    public DefinePrSubjectRepository() {
        super("ExamMaster", "setup", "DefinePrSubject", "ExamMaster/SettingBasic/DefinePrSubject.jsx");
    }
}