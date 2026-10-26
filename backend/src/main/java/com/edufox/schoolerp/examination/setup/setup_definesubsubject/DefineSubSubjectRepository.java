package com.edufox.schoolerp.examination.setup.setup_definesubsubject;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Setup/DefineSubSubject.jsx. */
@Repository("page_com_edufox_schoolerp_examination_setup_setup_definesubsubject_definesubsubjectrepository_java")
public class DefineSubSubjectRepository extends PageArtifactRepository {
    public DefineSubSubjectRepository() {
        super("ExamMaster", "setup", "DefineSubSubject", "ExamMaster/Setup/DefineSubSubject.jsx");
    }
}