package com.edufox.schoolerp.examination.setup.setup_defineattrib;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Setup/DefineAttrib.jsx. */
@Repository("page_com_edufox_schoolerp_examination_setup_setup_defineattrib_defineattribrepository_java")
public class DefineAttribRepository extends PageArtifactRepository {
    public DefineAttribRepository() {
        super("ExamMaster", "setup", "DefineAttrib", "ExamMaster/Setup/DefineAttrib.jsx");
    }
}