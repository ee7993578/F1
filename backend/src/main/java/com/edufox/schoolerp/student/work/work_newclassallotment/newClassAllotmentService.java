package com.edufox.schoolerp.student.work.work_newclassallotment;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Work/newClassAllotment.jsx. */
@Service("page_student_work_work_newclassallotment_newclassallotmentservice_java")
public class newClassAllotmentService extends PageArtifactService<newClassAllotmentRepository> {
    public newClassAllotmentService(newClassAllotmentRepository repository) {
        super(repository);
    }
}