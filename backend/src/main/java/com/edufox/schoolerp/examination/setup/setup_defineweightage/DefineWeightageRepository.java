package com.edufox.schoolerp.examination.setup.setup_defineweightage;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Setup/DefineWeightage.jsx. */
@Repository("page_com_edufox_schoolerp_examination_setup_setup_defineweightage_defineweightagerepository_java")
public class DefineWeightageRepository extends PageArtifactRepository {
    public DefineWeightageRepository() {
        super("ExamMaster", "setup", "DefineWeightage", "ExamMaster/Setup/DefineWeightage.jsx");
    }
}