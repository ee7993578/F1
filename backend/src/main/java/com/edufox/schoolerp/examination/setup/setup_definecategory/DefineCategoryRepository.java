package com.edufox.schoolerp.examination.setup.setup_definecategory;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Setup/DefineCategory.jsx. */
@Repository("page_com_edufox_schoolerp_examination_setup_setup_definecategory_definecategoryrepository_java")
public class DefineCategoryRepository extends PageArtifactRepository {
    public DefineCategoryRepository() {
        super("ExamMaster", "setup", "DefineCategory", "ExamMaster/Setup/DefineCategory.jsx");
    }
}