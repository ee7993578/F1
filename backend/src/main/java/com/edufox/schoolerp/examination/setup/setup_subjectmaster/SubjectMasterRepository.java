package com.edufox.schoolerp.examination.setup.setup_subjectmaster;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Setup/SubjectMaster.jsx. */
@Repository("page_com_edufox_schoolerp_examination_setup_setup_subjectmaster_subjectmasterrepository_java")
public class SubjectMasterRepository extends PageArtifactRepository {
    public SubjectMasterRepository() {
        super("ExamMaster", "setup", "SubjectMaster", "ExamMaster/Setup/SubjectMaster.jsx");
    }
}