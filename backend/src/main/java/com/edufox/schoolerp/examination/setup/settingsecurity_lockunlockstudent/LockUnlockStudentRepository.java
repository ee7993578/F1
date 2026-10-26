package com.edufox.schoolerp.examination.setup.settingsecurity_lockunlockstudent;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/SettingSecurity/LockUnlockStudent.jsx. */
@Repository("page_com_edufox_schoolerp_examination_setup_settingsecurity_lockunlockstudent_lockunlockstudentrepository_java")
public class LockUnlockStudentRepository extends PageArtifactRepository {
    public LockUnlockStudentRepository() {
        super("ExamMaster", "setup", "LockUnlockStudent", "ExamMaster/SettingSecurity/LockUnlockStudent.jsx");
    }
}