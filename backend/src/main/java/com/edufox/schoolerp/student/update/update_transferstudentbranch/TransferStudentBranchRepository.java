package com.edufox.schoolerp.student.update.update_transferstudentbranch;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Update/TransferStudentBranch.jsx. */
@Repository("page_com_edufox_schoolerp_student_update_update_transferstudentbranch_transferstudentbranchrepository_java")
public class TransferStudentBranchRepository extends PageArtifactRepository {
    public TransferStudentBranchRepository() {
        super("Student", "update", "TransferStudentBranch", "Student/Update/TransferStudentBranch.jsx");
    }
}