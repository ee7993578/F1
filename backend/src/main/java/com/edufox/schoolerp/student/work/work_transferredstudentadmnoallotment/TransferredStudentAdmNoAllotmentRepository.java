package com.edufox.schoolerp.student.work.work_transferredstudentadmnoallotment;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Work/TransferredStudentAdmNoAllotment.jsx. */
@Repository("page_com_edufox_schoolerp_student_work_work_transferredstudentadmnoallotment_transferredstudentadmnoallotmentrepository_java")
public class TransferredStudentAdmNoAllotmentRepository extends PageArtifactRepository {
    public TransferredStudentAdmNoAllotmentRepository() {
        super("Student", "work", "TransferredStudentAdmNoAllotment", "Student/Work/TransferredStudentAdmNoAllotment.jsx");
    }
}