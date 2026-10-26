package com.edufox.schoolerp.examination.setup.setup_define_classsubject;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Setup/Define_ClassSubject.jsx. */
@Repository("page_com_edufox_schoolerp_examination_setup_setup_define_classsubject_define_classsubjectrepository_java")
public class Define_ClassSubjectRepository extends PageArtifactRepository {
    public Define_ClassSubjectRepository() {
        super("ExamMaster", "setup", "Define_ClassSubject", "ExamMaster/Setup/Define_ClassSubject.jsx");
    }
}