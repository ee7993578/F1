package com.edufox.schoolerp.examination.setup.setup_definemaxmin;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Setup/DefineMaxMin.jsx. */
@Repository("page_com_edufox_schoolerp_examination_setup_setup_definemaxmin_definemaxminrepository_java")
public class DefineMaxMinRepository extends PageArtifactRepository {
    public DefineMaxMinRepository() {
        super("ExamMaster", "setup", "DefineMaxMin", "ExamMaster/Setup/DefineMaxMin.jsx");
    }
}