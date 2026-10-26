package com.edufox.schoolerp.examination.setup.setup_defineprsubject;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Setup/DefinePrSubject.jsx. */
@Repository("page_com_edufox_schoolerp_examination_setup_setup_defineprsubject_defineprsubjectrepository_java")
public class DefinePrSubjectRepository extends PageArtifactRepository {
    public DefinePrSubjectRepository() {
        super("ExamMaster", "setup", "DefinePrSubject", "ExamMaster/Setup/DefinePrSubject.jsx");
    }
}