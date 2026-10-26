package com.edufox.schoolerp.student.update.update_transferstudentbranch;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Update/TransferStudentBranch.jsx. */
@RestController("page_student_update_update_transferstudentbranch_transferstudentbranchcontroller_java")
@RequestMapping("/api/page-artifacts/student/update/update_transferstudentbranch")
public class TransferStudentBranchController extends PageArtifactController<TransferStudentBranchService> {
    public TransferStudentBranchController(TransferStudentBranchService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}