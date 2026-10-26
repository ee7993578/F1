package com.edufox.schoolerp.examination.setup.settingprimary_definecategory;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/SettingPrimary/DefineCategory.jsx. */
@Repository("page_com_edufox_schoolerp_examination_setup_settingprimary_definecategory_definecategoryrepository_java")
public class DefineCategoryRepository extends PageArtifactRepository {
    public DefineCategoryRepository() {
        super("ExamMaster", "setup", "DefineCategory", "ExamMaster/SettingPrimary/DefineCategory.jsx");
    }
}