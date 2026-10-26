package com.edufox.schoolerp.examination.setup.setup_indicatorcategory;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Setup/IndicatorCategory.jsx. */
@Repository("page_com_edufox_schoolerp_examination_setup_setup_indicatorcategory_indicatorcategoryrepository_java")
public class IndicatorCategoryRepository extends PageArtifactRepository {
    public IndicatorCategoryRepository() {
        super("ExamMaster", "setup", "IndicatorCategory", "ExamMaster/Setup/IndicatorCategory.jsx");
    }
}