package com.edufox.schoolerp.student.work.work_admnoallotment;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Work/AdmNoAllotment.jsx. */
@Service("page_student_work_work_admnoallotment_admnoallotmentservice_java")
public class AdmNoAllotmentService extends PageArtifactService<AdmNoAllotmentRepository> {
    public AdmNoAllotmentService(AdmNoAllotmentRepository repository) {
        super(repository);
    }
}