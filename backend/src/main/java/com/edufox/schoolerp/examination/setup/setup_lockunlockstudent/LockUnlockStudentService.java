package com.edufox.schoolerp.examination.setup.setup_lockunlockstudent;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Setup/LockUnlockStudent.jsx. */
@Service("page_examination_setup_setup_lockunlockstudent_lockunlockstudentservice_java")
public class LockUnlockStudentService extends PageArtifactService<LockUnlockStudentRepository> {
    public LockUnlockStudentService(LockUnlockStudentRepository repository) {
        super(repository);
    }
}