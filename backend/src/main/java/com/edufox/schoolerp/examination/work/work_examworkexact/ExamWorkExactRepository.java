package com.edufox.schoolerp.examination.work.work_examworkexact;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Work/ExamWorkExact.jsx. */
@Repository("page_com_edufox_schoolerp_examination_work_work_examworkexact_examworkexactrepository_java")
public class ExamWorkExactRepository extends PageArtifactRepository {
    public ExamWorkExactRepository() {
        super("ExamMaster", "work", "ExamWorkExact", "ExamMaster/Work/ExamWorkExact.jsx");
    }
}