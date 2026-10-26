package com.edufox.schoolerp.student.work.work_admnoallotment;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Work/AdmNoAllotment.jsx. */
@Repository("page_com_edufox_schoolerp_student_work_work_admnoallotment_admnoallotmentrepository_java")
public class AdmNoAllotmentRepository extends PageArtifactRepository {
    public AdmNoAllotmentRepository() {
        super("Student", "work", "AdmNoAllotment", "Student/Work/AdmNoAllotment.jsx");
    }
}