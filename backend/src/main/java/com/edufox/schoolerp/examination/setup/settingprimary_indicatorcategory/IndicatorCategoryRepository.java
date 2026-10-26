package com.edufox.schoolerp.examination.setup.settingprimary_indicatorcategory;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/SettingPrimary/IndicatorCategory.jsx. */
@Repository("page_com_edufox_schoolerp_examination_setup_settingprimary_indicatorcategory_indicatorcategoryrepository_java")
public class IndicatorCategoryRepository extends PageArtifactRepository {
    public IndicatorCategoryRepository() {
        super("ExamMaster", "setup", "IndicatorCategory", "ExamMaster/SettingPrimary/IndicatorCategory.jsx");
    }
}