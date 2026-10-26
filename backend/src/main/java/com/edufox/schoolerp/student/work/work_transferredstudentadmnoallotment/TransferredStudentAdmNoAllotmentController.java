package com.edufox.schoolerp.student.work.work_transferredstudentadmnoallotment;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Work/TransferredStudentAdmNoAllotment.jsx. */
@RestController("page_student_work_work_transferredstudentadmnoallotment_transferredstudentadmnoallotmentcontroller_java")
@RequestMapping("/api/page-artifacts/student/work/work_transferredstudentadmnoallotment")
public class TransferredStudentAdmNoAllotmentController extends PageArtifactController<TransferredStudentAdmNoAllotmentService> {
    public TransferredStudentAdmNoAllotmentController(TransferredStudentAdmNoAllotmentService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}