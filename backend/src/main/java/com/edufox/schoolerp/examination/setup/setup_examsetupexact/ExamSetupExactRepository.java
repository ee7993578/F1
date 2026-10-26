package com.edufox.schoolerp.examination.setup.setup_examsetupexact;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Setup/ExamSetupExact.jsx. */
@Repository("page_com_edufox_schoolerp_examination_setup_setup_examsetupexact_examsetupexactrepository_java")
public class ExamSetupExactRepository extends PageArtifactRepository {
    public ExamSetupExactRepository() {
        super("ExamMaster", "setup", "ExamSetupExact", "ExamMaster/Setup/ExamSetupExact.jsx");
    }
}