package com.edufox.schoolerp.examination.setup.setup_lockunlockstudent;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Setup/LockUnlockStudent.jsx. */
@Repository("page_com_edufox_schoolerp_examination_setup_setup_lockunlockstudent_lockunlockstudentrepository_java")
public class LockUnlockStudentRepository extends PageArtifactRepository {
    public LockUnlockStudentRepository() {
        super("ExamMaster", "setup", "LockUnlockStudent", "ExamMaster/Setup/LockUnlockStudent.jsx");
    }
}