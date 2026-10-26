package com.edufox.schoolerp.student.work.work_transferredstudentadmnoallotment;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Work/TransferredStudentAdmNoAllotment.jsx. */
@Service("page_student_work_work_transferredstudentadmnoallotment_transferredstudentadmnoallotmentservice_java")
public class TransferredStudentAdmNoAllotmentService extends PageArtifactService<TransferredStudentAdmNoAllotmentRepository> {
    public TransferredStudentAdmNoAllotmentService(TransferredStudentAdmNoAllotmentRepository repository) {
        super(repository);
    }
}