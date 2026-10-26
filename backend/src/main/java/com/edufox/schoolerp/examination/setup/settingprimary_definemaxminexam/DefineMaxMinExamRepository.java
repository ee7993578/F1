package com.edufox.schoolerp.examination.setup.settingprimary_definemaxminexam;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/SettingPrimary/DefineMaxMinExam.jsx. */
@Repository("page_com_edufox_schoolerp_examination_setup_settingprimary_definemaxminexam_definemaxminexamrepository_java")
public class DefineMaxMinExamRepository extends PageArtifactRepository {
    public DefineMaxMinExamRepository() {
        super("ExamMaster", "setup", "DefineMaxMinExam", "ExamMaster/SettingPrimary/DefineMaxMinExam.jsx");
    }
}