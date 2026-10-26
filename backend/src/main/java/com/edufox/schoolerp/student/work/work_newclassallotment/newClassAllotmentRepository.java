package com.edufox.schoolerp.student.work.work_newclassallotment;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Work/newClassAllotment.jsx. */
@Repository("page_com_edufox_schoolerp_student_work_work_newclassallotment_newclassallotmentrepository_java")
public class newClassAllotmentRepository extends PageArtifactRepository {
    public newClassAllotmentRepository() {
        super("Student", "work", "newClassAllotment", "Student/Work/newClassAllotment.jsx");
    }
}