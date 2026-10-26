package com.edufox.schoolerp.student.update.update_transferstudentbranch;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Update/TransferStudentBranch.jsx. */
@Service("page_student_update_update_transferstudentbranch_transferstudentbranchservice_java")
public class TransferStudentBranchService extends PageArtifactService<TransferStudentBranchRepository> {
    public TransferStudentBranchService(TransferStudentBranchRepository repository) {
        super(repository);
    }
}